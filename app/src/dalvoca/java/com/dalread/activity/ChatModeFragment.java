package com.dalread.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dalread.R;
import com.dalread.base.BaseChatDetailsFragment;
import com.dalread.base.EnumMessageType;
import com.dalread.dialog.SendOrRecordAgainDialog;
import com.dalread.helper.FirestoreHelper;
import com.dalread.holder.IncomingVoiceMessageViewHolder;
import com.dalread.holder.OutcomingVoiceMessageViewHolder;
import com.dalread.listener.OnChatVoiceMessageClickListener;
import com.dalread.model.ChatMessage;
import com.dalread.model.ChatPhoto;
import com.dalread.model.ChatRoomInfo;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.Loading;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;

public class ChatModeFragment extends BaseChatDetailsFragment {

    private static final byte CONTENT_TYPE_VOICE = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_RECORDING = PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_PHOTO = REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_RECORDING + 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOAD_PHOTO = REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_PHOTO + 1;

    @BindView(R.id.v_messages_list)
    MessagesList vMessagesList;
    @BindView(R.id.v_message_input)
    MessageInput vMessageInput;

    @BindString(R.string.app_name)
    String appName;

    private Context context;
    private Bundle bundle;
    private ChatRoomInfo chatRoomInfo;
    private FirestoreHelper firestoreHelper;
    private File chatFolder;
    private MessagesListAdapter<ChatMessage> adapter;
    private MediaPlayer mediaPlayer;
    private ChatMessage playingMessage;
    private SendOrRecordAgainDialog sendOrRecordAgainDialog;
    private Uri photoUri;
    private ChatMessage photoMessage;
    private QueryDocumentSnapshot lastVisibleDocumentSnapshot;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_chat_mode;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();

        getFirestoreData();
    }

    private void initData() {
        context = getContext();
        bundle = getArguments();
        chatRoomInfo = (ChatRoomInfo) Objects.requireNonNull(bundle).getSerializable(Constant.BUNDLE.KEY_CHAT_ROOM_INFO);
        chatFolder = Voca.getChatFolderOnLocal(context);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void initLayout() {
        activity.setToolbarTitle(chatRoomInfo.getChatRoomName());
        adapter = new MessagesListAdapter<>(
                String.valueOf(sharedPreferences.getRealUid()),
                new MessageHolders()
                        .registerContentType(
                                CONTENT_TYPE_VOICE,
                                IncomingVoiceMessageViewHolder.class, onChatVoiceMessageClickListener, R.layout.item_custom_incoming_voice_message,
                                OutcomingVoiceMessageViewHolder.class, onChatVoiceMessageClickListener, R.layout.item_custom_outcoming_voice_message,
                                (MessageHolders.ContentChecker<ChatMessage>) (message, type) -> {
                                    if (type == CONTENT_TYPE_VOICE)
                                        return message.getType() == EnumMessageType.VOICE.getId() && !TextUtils.isEmpty(message.getDownloadURL());
                                    return false;
                                }),
                new ImageLoader() {

                    @Override
                    public void loadImage(final ImageView imageView, @Nullable String url, @Nullable Object payload) {
                        final File file = Voca.getChatFileOnLocal(chatFolder, url);
                        if (file != null) {
                            if (file.exists()) {
                                startLoadImage(file, imageView);
                            } else {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(url);
                                storageReference.getFile(file)
                                        .addOnSuccessListener(taskSnapshot -> startLoadImage(file, imageView));
                            }
                        }
                    }

                    private void startLoadImage(File file, ImageView imageView) {
                        Glide.with(context).load(file).into(imageView);
                    }
                }
        );
        adapter.setOnMessageClickListener(message -> {
            int type = message.getType();
            if (type == EnumMessageType.TEXT.getId()) {
                if (message.getSenderId().equals(sharedPreferences.getUid())) {
                    new android.app.AlertDialog.Builder(context)
                            .setItems(R.array.outcome_text_message_options, (dialog, which) -> {
                                switch (which) {
                                    case Constant.OUTCOME_TEXT_MESSAGE_OPTIONS.COPY:
                                        copyTextMessage(message);
                                        break;
                                    case Constant.OUTCOME_TEXT_MESSAGE_OPTIONS.REPLY:
                                        replyTextMessage(message);
                                        break;
                                    case Constant.OUTCOME_TEXT_MESSAGE_OPTIONS.EDIT:
                                        editTextMessage(message);
                                        break;
                                    case Constant.OUTCOME_TEXT_MESSAGE_OPTIONS.DELETE:
                                        deleteMessage(message);
                                        break;
                                }
                            })
                            .show();
                } else {
                    new android.app.AlertDialog.Builder(context)
                            .setItems(R.array.income_text_message_options, (dialog, which) -> {
                                switch (which) {
                                    case Constant.INCOME_TEXT_MESSAGE_OPTIONS.COPY:
                                        copyTextMessage(message);
                                        break;
                                    case Constant.INCOME_TEXT_MESSAGE_OPTIONS.REPLY:
                                        replyTextMessage(message);
                                        break;
                                }
                            })
                            .show();
                }
            } else if (type == EnumMessageType.PHOTO.getId()) {
                if (message.getSenderId().equals(sharedPreferences.getUid())) {
                    new android.app.AlertDialog.Builder(context)
                            .setItems(R.array.outcome_photo_message_options, (dialog, which) -> {
                                switch (which) {
                                    case Constant.OUTCOME_PHOTO_MESSAGE_OPTIONS.DELETE:
                                        deleteMessage(message);
                                        break;
                                    case Constant.OUTCOME_PHOTO_MESSAGE_OPTIONS.OPEN:
                                        openPhotoViewer(message);
                                        break;
                                    case Constant.OUTCOME_PHOTO_MESSAGE_OPTIONS.DOWNLOAD:
                                        downloadOriginalPhoto(message);
                                        break;
                                }
                            })
                            .show();
                } else {
                    new android.app.AlertDialog.Builder(context)
                            .setItems(R.array.income_photo_message_options, (dialog, which) -> {
                                switch (which) {
                                    case Constant.INCOME_PHOTO_MESSAGE_OPTIONS.OPEN:
                                        openPhotoViewer(message);
                                        break;
                                    case Constant.INCOME_PHOTO_MESSAGE_OPTIONS.DOWNLOAD:
                                        downloadOriginalPhoto(message);
                                        break;
                                }
                            })
                            .show();
                }
            } else if (type == EnumMessageType.VOICE.getId()) {
                if (message.getSenderId().equals(sharedPreferences.getUid())) {
                    new android.app.AlertDialog.Builder(context)
                            .setItems(R.array.outcome_voice_message_options, (dialog, which) -> {
                                switch (which) {
                                    case Constant.OUTCOME_VOICE_MESSAGE_OPTIONS.DELETE:
                                        deleteMessage(message);
                                        break;
                                    case Constant.OUTCOME_VOICE_MESSAGE_OPTIONS.DOWNLOAD:
                                        downloadAndPlayVoice(message);
                                        break;
                                }
                            })
                            .show();
                } else {
                    new android.app.AlertDialog.Builder(context)
                            .setItems(R.array.income_voice_message_options, (dialog, which) -> {
                                if (which == Constant.INCOME_VOICE_MESSAGE_OPTIONS.DOWNLOAD) {
                                    downloadAndPlayVoice(message);
                                }
                            })
                            .show();
                }
            }
        });
        vMessagesList.setAdapter(adapter);
        vMessagesList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleDocumentSnapshot != null
                        && recyclerView.getLayoutManager() instanceof LinearLayoutManager
                        && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    firestoreHelper.listenToMessageList(querySnapshotEventLoadMoreListener, lastVisibleDocumentSnapshot);
                }
            }
        });

        vMessageInput.setInputListener(input -> {
            if (firestoreHelper == null)
                return false;

            String text = input.toString();
            if (TextUtils.isEmpty(text))
                return false;

            if (TextUtils.isEmpty(firestoreHelper.getEditedMessageId())) {
                firestoreHelper.addTextMessage(text, null, null);
            } else {
                firestoreHelper.editTextMessage(text, null, null);
            }
            return true;
        });
        vMessageInput.setAttachmentsListener(() -> new android.app.AlertDialog.Builder(context)
                .setItems(R.array.attachment_options, (dialog, which) -> {
                    switch (which) {
                        case Constant.ATTACHMENT.PHOTO:
                            chooseImageFromGallery();
                            break;
                        case Constant.ATTACHMENT.CAMERA:
                            takePhotoFromCamera();
                            break;
                        case Constant.ATTACHMENT.RECORDING:
                            openRecordDialog();
                            break;
                    }
                })
                .show());
        sendOrRecordAgainDialog = new SendOrRecordAgainDialog(
                context,
                chatFolder,
                chatRoomInfo.getFirestoreChatRoomId(),
                sharedPreferences.getRealUid(),
                (downloadURL, recordFile) -> {
                    if (recordFile != null && recordFile.exists()) {
                        // get voice file duration
                        String dur = Voca.getVoiceFileDuration(recordFile.getPath());
                        long durationInMillis = Long.parseLong(dur);
                        final String duration = DateUtils.getTimeToMinuteFormat().format(new Date(durationInMillis));
                        // create fake message to display fast
                        if (firestoreHelper != null) {
                            adapter.addToStart(firestoreHelper.createFakeVoiceMessage(downloadURL, duration), true);
                        }
                        // upload real message
                        Uri uri = Uri.fromFile(recordFile);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(downloadURL);
                        StorageMetadata metadata = new StorageMetadata.Builder()
                                .setContentType("application/octet-stream")
                                .build();
                        UploadTask uploadTask = storageReference.putFile(uri, metadata);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            if (firestoreHelper != null) {
                                firestoreHelper.addVoiceMessage(
                                        downloadURL,
                                        duration,
                                        null,
                                        null
                                );
                            }
                        }).addOnFailureListener(exception -> {
                        });
                    }
                }
        );
    }

    private void getFirestoreData() {
        firestoreHelper = new FirestoreHelper(context, sharedPreferences, chatRoomInfo.getFirestoreChatRoomId());
        firestoreHelper.listenToMessageList(querySnapshotEventListener);
    }

    private EventListener<QuerySnapshot> querySnapshotEventListener = new EventListener<QuerySnapshot>() {

        @Override
        public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
            if (e != null) {
                e.printStackTrace();
                return;
            }
            List<DocumentChange> dcs = snapshots.getDocumentChanges();
            for (int i = dcs.size() - 1; i >= 0; i--) {
                DocumentChange dc = dcs.get(i);
                if (lastVisibleDocumentSnapshot == null) {
                    lastVisibleDocumentSnapshot = dc.getDocument();
                }
                switch (dc.getType()) {
                    case ADDED:
                        addChatMessageToLayout(dc.getDocument());
                        break;
                    case MODIFIED:
                        updateChatMessageOnLayout(dc.getDocument());
                        break;
                    case REMOVED:
                        removeChatMessageOnLayout(dc.getDocument());
                        break;
                    default:
                        break;
                }
            }
            if (bundle.containsKey(Constant.BUNDLE.KEY_LIST_STATE)) {
                vMessagesList.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE));
                bundle.remove(Constant.BUNDLE.KEY_LIST_STATE);
            }
            if (bundle.containsKey(Constant.BUNDLE.KEY_INPUT_STATE)) {
                vMessageInput.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_INPUT_STATE));
                bundle.remove(Constant.BUNDLE.KEY_INPUT_STATE);
            }
        }
    };

    private EventListener<QuerySnapshot> querySnapshotEventLoadMoreListener = new EventListener<QuerySnapshot>() {

        @Override
        public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
            if (e != null) {
                e.printStackTrace();
                return;
            }
            List<QueryDocumentSnapshot> addedDocuments = new ArrayList<>();
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                lastVisibleDocumentSnapshot = dc.getDocument();
                switch (dc.getType()) {
                    case ADDED:
                        addedDocuments.add(dc.getDocument());
                        break;
                    case MODIFIED:
                        updateChatMessageOnLayout(dc.getDocument());
                        break;
                    case REMOVED:
                        removeChatMessageOnLayout(dc.getDocument());
                        break;
                    default:
                        break;
                }
            }
            if (!addedDocuments.isEmpty()) {
                addChatMessageToLayout(addedDocuments);
            }
            if (bundle.containsKey(Constant.BUNDLE.KEY_LIST_STATE)) {
                vMessagesList.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE));
                bundle.remove(Constant.BUNDLE.KEY_LIST_STATE);
            }
            if (bundle.containsKey(Constant.BUNDLE.KEY_INPUT_STATE)) {
                vMessageInput.restoreHierarchyState(bundle.getSparseParcelableArray(Constant.BUNDLE.KEY_INPUT_STATE));
                bundle.remove(Constant.BUNDLE.KEY_INPUT_STATE);
            }
        }
    };

    private void addChatMessageToLayout(QueryDocumentSnapshot document) {
        ChatMessage chatMessage = document.toObject(ChatMessage.class);
        chatMessage.setMessageId(document.getId());
        if (chatMessage.getType() == EnumMessageType.VOICE.getId()) {
            String fakeId = chatMessage.getDownloadURL();
            if (adapter.update(fakeId, chatMessage)) {
                // if goes here, fakeMessage has been replaced by chatMessage
                // fakeMessage's id was set to downloadURL, see: createFakeVoiceMessage(...)
                if (playingMessage != null && playingMessage.getId().equals(fakeId)) {
                    playingMessage = chatMessage;
                }
                return;
            }
        } else if (chatMessage.getType() == EnumMessageType.PHOTO.getId()) {
            String fakeId = chatMessage.getDownloadURL();
            if (adapter.update(fakeId, chatMessage)) {
                // if goes here, fakeMessage has been replaced by chatMessage
                // fakeMessage's id was set to downloadURL, see: createFakeVoiceMessage(...)
                return;
            }
        }
        adapter.addToStart(chatMessage, true);
    }

    private void addChatMessageToLayout(List<QueryDocumentSnapshot> documents) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            ChatMessage chatMessage = document.toObject(ChatMessage.class);
            chatMessage.setMessageId(document.getId());
            chatMessages.add(chatMessage);
        }
        adapter.addToEnd(chatMessages, false);
    }

    private void updateChatMessageOnLayout(QueryDocumentSnapshot document) {
        ChatMessage chatMessage = document.toObject(ChatMessage.class);
        chatMessage.setMessageId(document.getId());
        adapter.update(chatMessage);
    }

    private void removeChatMessageOnLayout(QueryDocumentSnapshot document) {
        adapter.deleteById(document.getId());
    }

    private OnChatVoiceMessageClickListener onChatVoiceMessageClickListener = this::downloadAndPlayVoice;

    private void playVoice(ChatMessage message, File file) {
        try {
            Uri uri = Uri.fromFile(file);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playingMessage = message;
            vMessagesList.post(playMessageRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopVoice() {
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.stop();
        mediaPlayer.reset();
        stopPlayingMessage();
    }

    private void stopPlayingMessage() {
        if (playingMessage != null) {
            playingMessage.setPlaying(false);
            playingMessage.setPlayingMillis(0);
            adapter.update(playingMessage);
            playingMessage = null;
        }
    }

    private Runnable playMessageRunnable = new Runnable() {

        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                if (playingMessage != null) {
                    playingMessage.setPlaying(true);
                    playingMessage.setPlayingMillis(mediaPlayer.getCurrentPosition());
                    playingMessage.setTotalMillis(mediaPlayer.getDuration());
                    adapter.update(playingMessage);
                }
                vMessagesList.postDelayed(playMessageRunnable, Constant.PLAY_VOICE_INTERVAL);
            }
        }
    };

    private void chooseImageFromGallery() {
        Voca.startChooseImageIntent(activity);
    }

    private void takePhotoFromCamera() {
        if (PermissionUtils.checkWriteExternalStorage(activity, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_PHOTO)) {
            File cacheFolder = new File(activity.getExternalCacheDir(), "camera");
            if (cacheFolder.exists() || cacheFolder.mkdirs()) {
                File photoFile = new File(cacheFolder, Constant.FILE.CAPTURED_IMAGE);
                photoUri = FileProvider.getUriForFile(context, activity.getPackageName() + ".provider", photoFile);
                Voca.startCameraIntent(activity, photoUri);
            }
        }
    }

    private void cropImage(Uri srcUri) {
        long now = System.currentTimeMillis();
        String hashCode = Voca.md5(String.valueOf(now));
        String downloadURL = Voca.getChatImageDowloadURL(
                chatRoomInfo.getFirestoreChatRoomId(),
                sharedPreferences.getRealUid(),
                hashCode
        );
        File destFile = Voca.getChatFileOnLocal(chatFolder, downloadURL);
        Uri destUri = Uri.fromFile(destFile);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(Constant.UCROP.CROP_COMPRESSION_QUALITY);
        options.setFreeStyleCropEnabled(Constant.UCROP.CROP_FREE_STYLE);
        UCrop.of(srcUri, destUri)
                .withOptions(options)
                .withMaxResultSize(Constant.UCROP.CROP_MAX_WIDTH, Constant.UCROP.CROP_MAX_HEIGHT)
                .start(activity);
    }

    private void openRecordDialog() {
        if (PermissionUtils.checkRecordAudio(activity, true)) {
            if (PermissionUtils.checkWriteExternalStorage(activity, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_RECORDING)) {
                if (sendOrRecordAgainDialog != null) {
                    sendOrRecordAgainDialog.show();
                }
            }
        }
    }

    private void onCropImageSuccess(Uri imageUri) {
        final String thumbnailPath = makeThumbnail(imageUri);
        if (TextUtils.isEmpty(thumbnailPath)) {
            DLog.i("onCropImageSuccess", "Make thumbnail failure!");
        } else {
            String folderPath = chatFolder.getPath();
            final String thumbnailURL = thumbnailPath.replace(folderPath, Constant.FILE.FOLDER_CHAT);
            String imagePath = imageUri.getPath();
            final String downloadURL = Objects.requireNonNull(imagePath).replace(folderPath, Constant.FILE.FOLDER_CHAT);
            // create fake message to display fast
            if (firestoreHelper != null) {
                ChatMessage fakePhotoMessage = firestoreHelper.createFakePhotoMessage(downloadURL, thumbnailURL);
                adapter.addToStart(fakePhotoMessage, true);
            }
            // upload real message
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(downloadURL);
            UploadTask photoTask = storageReference.putFile(imageUri);
            photoTask.addOnSuccessListener(taskSnapshot -> {
                File thumbnailFile = new File(thumbnailPath);
                Uri thumbnailUri = Uri.fromFile(thumbnailFile);
                StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(thumbnailURL);
                UploadTask thumbnailTask = storageReference1.putFile(thumbnailUri);
                thumbnailTask.addOnSuccessListener(taskSnapshot1 -> {
                    if (firestoreHelper != null) {
                        firestoreHelper.addPhotoMessage(
                                downloadURL,
                                thumbnailURL,
                                null,
                                null
                        );
                    }
                }).addOnFailureListener(exception -> DLog.i("onCropImageSuccess", "Upload thumbnail failure!"));
            }).addOnFailureListener(exception -> DLog.i("onCropImageSuccess", "Upload photo failure!"));
        }
    }

    private String makeThumbnail(Uri imageUri) {
        try {
            String imagePath = imageUri.getPath();
            String thumbnailPath = Voca.addSuffixThumbnail(imagePath);
            FileOutputStream out = new FileOutputStream(thumbnailPath);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
            float thumbnailRate = bitmap.getWidth() / (float) Constant.UCROP.THUMBNAIL_MAX_WIDTH;
            int thumbnailWidth, thumbnailHeight;
            if (thumbnailRate > 1) {
                thumbnailWidth = Math.round(bitmap.getWidth() / thumbnailRate);
                thumbnailHeight = Math.round(bitmap.getHeight() / thumbnailRate);
            } else {
                thumbnailWidth = bitmap.getWidth();
                thumbnailHeight = bitmap.getHeight();
            }
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(
                    bitmap,
                    thumbnailWidth,
                    thumbnailHeight
            );
            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, Constant.UCROP.THUMBNAIL_COMPRESSION_QUALITY, out);
            out.flush();
            out.close();
            return thumbnailPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void copyTextMessage(ChatMessage message) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(appName, message.getContent()));
            ToastUtil.getInstance(context).show( R.string.copied);
        }
    }

    private void replyTextMessage(ChatMessage message) {
        if (firestoreHelper != null) {
            firestoreHelper.setEditedMessageId("");
        }
        EditText editText = vMessageInput.getInputEditText();
        String text = message.getContent() + "\n--------------------------------\n";
        editText.setText(text);
        editText.setSelection(text.length());
        editText.requestFocus();
        Utils.showSoftKeyboard(context, editText);
    }

    private void editTextMessage(ChatMessage message) {
        if (firestoreHelper != null) {
            firestoreHelper.setEditedMessageId(message.getMessageId());
        }
        EditText editText = vMessageInput.getInputEditText();
        String text = message.getContent();
        editText.setText(text);
        editText.setSelection(text.length());
        editText.requestFocus();
        Utils.showSoftKeyboard(context, editText);
    }

    private void deleteMessage(ChatMessage message) {
        if (firestoreHelper != null) {
            firestoreHelper.setEditedMessageId("");
            firestoreHelper.deleteMessage(message.getMessageId(), null, null);
        }
    }

    private void downloadAndPlayVoice(final ChatMessage message) {
        boolean isPlaying = playingMessage != null && playingMessage.getId().equals(message.getId());
        stopVoice();
        if (!isPlaying) {
            String downloadURL = message.getDownloadURL();
            final File localFile = Voca.getChatFileOnLocal(chatFolder, downloadURL);
            if (localFile.exists()) {
                playVoice(message, localFile);
            } else {
                Loading.show(context);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(downloadURL);
                storageReference.getFile(localFile)
                        .addOnSuccessListener(taskSnapshot -> {
                            Loading.hide();
                            playVoice(message, localFile);
                        }).addOnFailureListener(e -> Loading.hide());
            }
        }
    }

    private void downloadOriginalPhoto(ChatMessage message) {
        if (PermissionUtils.checkWriteExternalStorage(activity, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOAD_PHOTO)) {
            String url = message.getDownloadURL();
            final File file = Voca.getChatFileOnLocal(chatFolder, url);
            if (file != null) {
                if (file.exists()) {
                    copyToSystemPhotoFolder(file);
                } else {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(url);
                    storageReference.getFile(file)
                            .addOnSuccessListener(taskSnapshot -> copyToSystemPhotoFolder(file));
                }
            }
            photoMessage = null;
        } else {
            photoMessage = message;
        }
    }

    private void copyToSystemPhotoFolder(File srcFile) {
        File destFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
        if (destFolder.exists() || destFolder.mkdirs()) {
            File destFile = new File(destFolder, srcFile.getName());
            try {
                Voca.copyFile(srcFile, destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openPhotoViewer(ChatMessage message) {
        final List<ChatPhoto> chatPhotos = new ArrayList<>();
        int pos = 0;
        String url = message.getDownloadURL();
        String folderPath = Voca.getChatFileOnLocal(chatFolder, url).getParent();
        if (!TextUtils.isEmpty(folderPath)) {
            File folder = new File(folderPath);
            if (folder.exists()) {
                File[] allFiles = folder.listFiles();
                if (allFiles != null && allFiles.length > 0) {
                    HashMap<String, ChatPhoto> dataMap = new HashMap<>();
                    final String imageSuffix = "." + Constant.FILE.EXTENTION_IMAGE;
                    final String thumbnailSuffix = Constant.FILE.SUFFIX_THUMBNAIL + imageSuffix;
                    for (File file : allFiles) {
                        String fileName = file.getName();
                        if (fileName.endsWith(imageSuffix)) {
                            String key;
                            if (fileName.endsWith(thumbnailSuffix)) {
                                key = fileName.replace(thumbnailSuffix, "");
                            } else {
                                key = fileName.replace(imageSuffix, "");
                            }
                            ChatPhoto chatPhoto;
                            if (dataMap.containsKey(key)) {
                                chatPhoto = dataMap.get(key);
                            } else {
                                chatPhoto = new ChatPhoto();
                                dataMap.put(key, chatPhoto);
                            }
                            Objects.requireNonNull(chatPhoto).setKey(key);
                            if (fileName.endsWith(thumbnailSuffix)) {
                                chatPhoto.setThumbnailFile(file);
                            } else {
                                chatPhoto.setOriginalFile(file);
                            }
                        }
                    }
                    for (String key : dataMap.keySet()) {
                        ChatPhoto chatPhoto = dataMap.get(key);
                        chatPhotos.add(chatPhoto);
                        if (url.contains(Objects.requireNonNull(chatPhoto).getKey())) {
                            pos = chatPhotos.size() - 1;
                        }
                    }
                }
            }
        }
        View overlayView = getLayoutInflater().inflate(R.layout.layout_image_viewer_overlay, null);
        final StfalconImageViewer<ChatPhoto> imageViewer = new StfalconImageViewer.Builder<>(context, chatPhotos, (imageView, chatPhoto) -> {
            File file = chatPhoto.getOriginalFile();
            if (file == null || !file.exists()) {
                file = chatPhoto.getThumbnailFile();
            }
            Glide.with(context).load(file).into(imageView);
        }).withStartPosition(pos)
                .withOverlayView(overlayView)
                .show();
        overlayView.findViewById(R.id.ic_left).setOnClickListener(v -> imageViewer.dismiss());
        overlayView.findViewById(R.id.tv_right).setOnClickListener(v -> {
            int pos1 = imageViewer.currentPosition();
            final ChatPhoto chatPhoto = chatPhotos.get(pos1);
            File thumbnailFile = chatPhoto.getThumbnailFile();
            final File originalFile = new File(thumbnailFile.getPath().replace(Constant.FILE.SUFFIX_THUMBNAIL, ""));
            String url1 = Constant.FILE.FOLDER_CHAT + originalFile.getPath().replace(chatFolder.getPath(), "");
            Loading.show(context);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(url1);
            storageReference.getFile(originalFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Loading.hide();
                        chatPhoto.setOriginalFile(originalFile);
                        imageViewer.updateImages(chatPhotos);
                        copyToSystemPhotoFolder(originalFile);
                    })
                    .addOnFailureListener(e -> Loading.hide());
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PermissionUtils.REQUEST_CODE_RECORD_AUDIO:
                case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_RECORDING:
                    openRecordDialog();
                    break;
                case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_PHOTO:
                    takePhotoFromCamera();
                    break;
                case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOAD_PHOTO:
                    downloadOriginalPhoto(photoMessage);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_CODE.SELECT_PHOTO:
                    if (data != null) {
                        cropImage(data.getData());
                    }
                    break;
                case Constant.REQUEST_CODE.CAMERA:
                    if (photoUri != null) {
                        cropImage(photoUri);
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    if (data != null) {
                        onCropImageSuccess(UCrop.getOutput(data));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (firestoreHelper != null) {
            firestoreHelper.stopListenToMessageList();
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (sendOrRecordAgainDialog != null) {
            sendOrRecordAgainDialog.onDestroy();
        }
        if (!activity.isFinishing()) {
            SparseArray<Parcelable> container = new SparseArray<>();
            vMessagesList.saveHierarchyState(container);
            bundle.putSparseParcelableArray(Constant.BUNDLE.KEY_LIST_STATE, container);
            container = new SparseArray<>();
            vMessageInput.saveHierarchyState(container);
            bundle.putSparseParcelableArray(Constant.BUNDLE.KEY_INPUT_STATE, container);
        }

        super.onDestroyView();
    }
}
