package com.dalread.util.arasubtitle;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.database.sqlite.model.SubtitleWordListModel;
import com.dalread.util.DLog;
import com.dalread.util.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTranslateFileService {
    protected SubDatabase dicDatabase; //영어 사전 DB
    protected SubDatabase subDatabase; //각 영화별 자막 DB

    public void setDicDatabase(SubDatabase dicDatabase) {
        this.dicDatabase = dicDatabase;
    }

    public void setSubDatabase(SubDatabase subDatabase) {
        this.subDatabase = subDatabase;
    }


    public void translateFileWithFixedNameDTO(File file, File file2, AbstractTranslateFileService translateFile2) {
        translateInputFileDTO(file, file2, translateFile2);
    }
    public static void copyFile(File source, File destination) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }

    protected String getResultFileExt() {
        return Constants.FILEEXT_epub;
    }

    protected String getOriginalFileNameDTO(DTO_INPUT_MAKE_RUBY dto) {
        return getFileNameToBeDTO(dto);
    }

//
//    protected String getOriginalFileName(MultipartFile file,HttpServletRequest req) {
//        if (file == null) {
//            return getFileNameToBe(req);
//        }
//        return file.getOriginalFilename();
//
//    }
//
//    protected void makeEPubFromTextDTO(String strOri, File src) {
//        return;
//    }
//
//    protected void makeEPubFromText(String strOri, File src, HttpServletRequest req, String folderNew) {
//        return;
//    }

    protected boolean unzip(String src) {
        return false;
    }
//    protected void translateInputFile(File FILE_input, HttpServletRequest req, String folderNew) {
//
//    }
    protected void translateInputFileDTO(File FILE_input, File FILE_input2, AbstractTranslateFileService translateFile2) {

    }
//
//    protected void translateInputFile2(File FILE_input, HttpServletRequest req, String folderNew) {
//
//    }
//
//    public void deleteDir(String strPathOfEPub) {
//        //작업이 다 끝났으면 epub가 있는 폴더를 지운다..
//        String parentFolderOfEPub = FilenameUtils.getFullPath(strPathOfEPub);
//        deleteDir(new File(parentFolderOfEPub));
//    }

    protected Integer getMilliseconds(LocalTime locaTime) {
        return (locaTime.getHour() * 3600 + locaTime.getMinute() * 60 + locaTime.getSecond()) * 1000 + (locaTime.getNano() / 1000000);
    }

    //지금은 자막을 전부 SENTENCE에 넣는데, 나중에는 단어는 DIC테이블에 넣어야 한다.
    protected void insertSubtitleToTableDTO(DTO_SUBTITLE_PARSED dtoSubtitleParsed) {

//		Map<String, JSONObject> mapSubtitleVocaID = (Map<String, JSONObject>)mapSubtitle.get(Constants.KEY_mapSubtitleVocaID);
        try {
//			String videoFileName = "";
//			String subtitleFileName = "";
//
//			MultipartFile file = null;
//			MultipartHttpServletRequest mreq= (MultipartHttpServletRequest)req;
//		 	Iterator<String> fileNames = mreq.getFileNames();
//			while(fileNames.hasNext()) {
//				String fileName = fileNames.next();
//				logger.warn("fileName : " + fileName);
//
//				file = mreq.getFile(fileName);
//				String originalFilename = file.getOriginalFilename();
//				videoFileName = originalFilename;
//				subtitleFileName = originalFilename;
//				break;
//			}
//
//
//	    	Integer uid = Integer.parseInt(getUID(req));
//	    	String studyLang = getStudyLang(req);
//	    	String dispMeaningLang = getDisyMeaningLang(req);
//
//
//			AbstractLanguage lang = langFactory.getLanguageService(studyLang);



            insertDialogueInDB(dtoSubtitleParsed);
//
//			//SUBTITLE_언어 테이블에는 해당 비디오의 해당 자막과 번역(있으면)넣는다. (SUBTITLE_FILE_ID + WORD_DIAPLAY가 유니크)
//			insertUntunedDialogueInDB(dtoSubtitleParsed, dto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SUBTITLE_언어 테이블에는 해당언어의 대사일때만 넣는다.
    private void insertDialogueInDB(DTO_SUBTITLE_PARSED dtoSubtitleParsed) {
        try {
            List<DTO_SQL_INSERT_TRANSLATION> listTunedDialogToInsert = new ArrayList<DTO_SQL_INSERT_TRANSLATION>();
            List<DTO_SQL_INSERT_TRANSLATION> listUntunedDialogToInsert = new ArrayList<DTO_SQL_INSERT_TRANSLATION>();
            getDialogToInsertDB(dtoSubtitleParsed, listTunedDialogToInsert, listUntunedDialogToInsert);

            if (listUntunedDialogToInsert.size() == 0)
                return;

            DalReadTextService dalReadTextService = new DalReadTextService();
//            String fld_Meaning = dalReadTextService.getUserDispMeaningLangFldName(dto.getLANG_MEANING());
//            //SUBTITLE_언어_UNTUNDED 테이블에는 해당 자막파일의 학습어의 모든 대사를 중복만 제거하고 언어와 안 맞아도 다 넣는다. 나중에 KNOW 및 BOOKMARK에 사용할려면 해당언어의 자막이 아니더라도 넣어두어야 한다.
//            //자막은 중복이 가능하기 때문에 KNOW의 경우도 중복으로 처리하면 가능하나, BOOKMARK의 경우는 하나를 북마크했는데 중복된 다른게 북마크되면 이상하다.
//            //또 모국어자막만 있을때는 KNOW와 북마크를 처리할려면 엄청 많은 데이타를 저장할수 밖에 없어서 일단은 중복은 제거하가 모국어 자막만 있을때는 안넣는다.
//            HashMap<String, Object> map2 = new HashMap<String, Object>();
//            map2.put(Constants.KEY_TBL_NAME, lang.getTbl_SUBTITLE_UNTUNED());
//            map2.put("fld_Meaning", fld_Meaning);
//            map2.put("list", listUntunedDialogToInsert);
//            Integer intResult = sqlSession.insert("DalReadTextMapper.insertDialogueInDIC_SUBTITLE", map2);



            // 여기는 해당 자막이 언어와 맞을때만 넣는다.
            if (listTunedDialogToInsert.size() > 0)  {
//                map2.put(Constants.KEY_TBL_NAME, getTbl_SUBTITLE());
//                map2.put("fld_Meaning", fld_Meaning);
//                map2.put("list", listTunedDialogToInsert);
//
//                Integer intResult2 = sqlSession.insert("DalReadTextMapper.insertDialogueInDIC_SUBTITLE", map2);

//                //기존의 것중 서버에 뜻이 있어도 현재 자막의 뜻으로 업데이트 해준다. QUERY에서 UPDATABLE이 1일때만 업데이트 해준다. 업데이트 안할거면 DB값을 바꾸면 된다.
//                for (DTO_SQL_INSERT_TRANSLATION dtoSqlInsertTranslation : listTunedDialogToInsert) {
//                    if (dtoSqlInsertTranslation.getMEANING().equals("")) {
//                        continue;
//                    }
//                    map2.put("voca", dtoSqlInsertTranslation.getVOCA());
////					map2.put("vocaWithoutPunct", dtoSqlInsertTranslation.getVOCA_WITHOUT_PUNCTUATION());
//                    map2.put("meaning", dtoSqlInsertTranslation.getMEANING());
//
//                    Integer intResult1 = sqlSession.update("DalReadTextMapper.updateDialogueInDIC_SUBTITLE", map2);
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected String getTbl_SUBTITLE() {
        return Constants.TBL_SUBTITLE_ENG;
    }
    protected String getTbl_DIC() {
        return Constants.TBL_DIC_ENG;
    }
    private void getDialogToInsertDB(DTO_SUBTITLE_PARSED dtoSubtitleParsed,
                                     List<DTO_SQL_INSERT_TRANSLATION> listTunedDialogToInsert,
                                     List<DTO_SQL_INSERT_TRANSLATION> listUntunedDialogToInsert) {
        Map<String, DTO_DIALOGUE> mapSubtitleInfo = dtoSubtitleParsed.getMAP_DIALOGUE_INFO();

        for (String key : mapSubtitleInfo.keySet()) {
            DTO_DIALOGUE dtoDialogue = mapSubtitleInfo.get(key);
            DTO_SQL_INSERT_TRANSLATION dtoSqlInsertTranslationUntuned = new DTO_SQL_INSERT_TRANSLATION.Builder()
                    .VOCA(dtoDialogue.getVOCA())
                    .VOCA_WITHOUT_PUNCTUATION(dtoDialogue.getVOCA_REMOVE_PUNCT())
                    .MEANING(dtoDialogue.getMEANING())
                    .build();

            if (!(dtoDialogue.getVOCA().equals(""))) {
                listUntunedDialogToInsert.add(dtoSqlInsertTranslationUntuned);
            }
            if (dtoDialogue.getVOCA_REMOVE_PUNCT().equals("") || (mapSubtitleInfo.get(key).getCORRECT_FORMAT_VOCA_TO_INSERT_DB() == Constants.CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO)) {
                continue;
            }
            listTunedDialogToInsert.add(dtoSqlInsertTranslationUntuned);
        }
    }

    //테이블로 부터 자막의 VOCA, VOCA_ID, VOCA_TYPE을 받아온다. BOOKMAKR, KNOW도 받아온다.
    protected void getSubtitleVocaIDDTO(DTO_SUBTITLE_PARSED dtoSubtitleParsed) {
        try {
//			List<String> vocaDisplayList = dtoSubtitleParsed.getLIST_DIALOGUE_STUDY_LANG().stream().filter(e->(!(e.equals("")))).collect(Collectors.toList());
            List<String> vocaDisplayListWithoutPunctuation = dtoSubtitleParsed.getLIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION().stream().filter(e->(!(e.equals("")))).collect(Collectors.toList());
            List<String> vocaDisplayListMeaning = dtoSubtitleParsed.getLIST_DIALOGUE_INFO().stream().map(DTO_DIALOGUE::getMEANING).filter(e->(!(e.equals("")))).collect(Collectors.toList());
            if ((vocaDisplayListWithoutPunctuation.size() == 0) && (vocaDisplayListMeaning.size() == 0))
                return;

            getSubtitleKnowAndBookmarkDTO(dtoSubtitleParsed);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public Integer convertLangNameToLangCode(String value) {
        int result = Constants.LANGCODE_EN;
        try {
            if (value.equals(Constants.LANG_AR)) {
                result = Constants.LANGCODE_AR;
            } else if (value.equals(Constants.LANG_BN)) {
                result = Constants.LANGCODE_BN;
            } else if (value.equals(Constants.LANG_CH_S)) {
                result = Constants.LANGCODE_CH_S;
            } else if (value.equals(Constants.LANG_CH_T)) {
                result = Constants.LANGCODE_CH_T;
            } else if (value.equals(Constants.LANG_CS)) {
                result = Constants.LANGCODE_CS;
            } else if (value.equals(Constants.LANG_DA)) {
                result = Constants.LANGCODE_DA;
            } else if (value.equals(Constants.LANG_DE)) {
                result = Constants.LANGCODE_DE;
            } else if (value.equals(Constants.LANG_EL)) {
                result = Constants.LANGCODE_EL;
            } else if (value.equals(Constants.LANG_EN)) {
                result = Constants.LANGCODE_EN;
            } else if (value.equals(Constants.LANG_ES)) {
                result = Constants.LANGCODE_ES;
            } else if (value.equals(Constants.LANG_FI)) {
                result = Constants.LANGCODE_FI;
            } else if (value.equals(Constants.LANG_FR)) {
                result = Constants.LANGCODE_FR;
            } else if (value.equals(Constants.LANG_HE)) {
                result = Constants.LANGCODE_HE;
            } else if (value.equals(Constants.LANG_HI)) {
                result = Constants.LANGCODE_HI;
            } else if (value.equals(Constants.LANG_HR)) {
                result = Constants.LANGCODE_HR;
            } else if (value.equals(Constants.LANG_HU)) {
                result = Constants.LANGCODE_HU;
            } else if (value.equals(Constants.LANG_ID)) {
                result = Constants.LANGCODE_ID;
            } else if (value.equals(Constants.LANG_IT)) {
                result = Constants.LANGCODE_IT;
            } else if (value.equals(Constants.LANG_JP)) {
                result = Constants.LANGCODE_JP;
            } else if (value.equals(Constants.LANG_KO)) {
                result = Constants.LANGCODE_KO;
            } else if (value.equals(Constants.LANG_NL)) {
                result = Constants.LANGCODE_NL;
            } else if (value.equals(Constants.LANG_NO)) {
                result = Constants.LANGCODE_NO;
            } else if (value.equals(Constants.LANG_PL)) {
                result = Constants.LANGCODE_PL;
            } else if (value.equals(Constants.LANG_PT)) {
                result = Constants.LANGCODE_PT;
            } else if (value.equals(Constants.LANG_RO)) {
                result = Constants.LANGCODE_RO;
            } else if (value.equals(Constants.LANG_RU)) {
                result = Constants.LANGCODE_RU;
            } else if (value.equals(Constants.LANG_SK)) {
                result = Constants.LANGCODE_SK;
            } else if (value.equals(Constants.LANG_SV)) {
                result = Constants.LANGCODE_SV;
            } else if (value.equals(Constants.LANG_TH)) {
                result = Constants.LANGCODE_TH;
            } else if (value.equals(Constants.LANG_TR)) {
                result = Constants.LANGCODE_TR;
            } else if (value.equals(Constants.LANG_UK)) {
                result = Constants.LANGCODE_UK;
            } else if (value.equals(Constants.LANG_VI)) {
                result = Constants.LANGCODE_VI;
            } else if (value.equals(Constants.LANG_HANJA)) {
                result = Constants.LANGCODE_HANJA;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public String getUserDispMeaningLangFldName(String dispMeaningLang) {
        return convertFldLangEngToFldName(dispMeaningLang);
    }

    public String convertFldLangEngToFldName(String langNameInEnglish) {
//        logger.info("langNameInEnglish : " + langNameInEnglish);
        String strDispMeaningFldName = Constants.FLD_MEANING_KO;
        switch (langNameInEnglish.toUpperCase()) {
            case Constants.LANG_AR:
                strDispMeaningFldName = Constants.FLD_MEANING_AR;
                break;
            case Constants.LANG_BN:
                strDispMeaningFldName = Constants.FLD_MEANING_BN;
                break;
            case Constants.LANG_CH_S:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_S;
                break;
            case Constants.LANG_CH_T:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_T;
                break;
            case Constants.LANG_CS:
                strDispMeaningFldName = Constants.FLD_MEANING_CS;
                break;
            case Constants.LANG_DA:
                strDispMeaningFldName = Constants.FLD_MEANING_DA;
                break;
            case Constants.LANG_DE:
                strDispMeaningFldName = Constants.FLD_MEANING_DE;
                break;
            case Constants.LANG_EL:
                strDispMeaningFldName = Constants.FLD_MEANING_EL;
                break;

            case Constants.LANG_EN:
                strDispMeaningFldName = Constants.FLD_MEANING_ENG;
                break;
            case Constants.LANG_ES:
                strDispMeaningFldName = Constants.FLD_MEANING_ES;
                break;
            case Constants.LANG_FI:
                strDispMeaningFldName = Constants.FLD_MEANING_FI;
                break;
            case Constants.LANG_FR:
                strDispMeaningFldName = Constants.FLD_MEANING_FR;
                break;
            case Constants.LANG_HE:
                strDispMeaningFldName = Constants.FLD_MEANING_HE;
                break;
            case Constants.LANG_HI:
                strDispMeaningFldName = Constants.FLD_MEANING_HI;
                break;
            case Constants.LANG_HR:
                strDispMeaningFldName = Constants.FLD_MEANING_HR;
                break;
            case Constants.LANG_HU:
                strDispMeaningFldName = Constants.FLD_MEANING_HU;
                break;
            case Constants.LANG_ID:
                strDispMeaningFldName = Constants.FLD_MEANING_ID;
                break;
            case Constants.LANG_IT:
                strDispMeaningFldName = Constants.FLD_MEANING_IT;
                break;
            case Constants.LANG_JP:
                strDispMeaningFldName = Constants.FLD_MEANING_JP;
                break;
            case Constants.LANG_KO:
                strDispMeaningFldName = Constants.FLD_MEANING_KO;
                break;
            case Constants.LANG_NL:
                strDispMeaningFldName = Constants.FLD_MEANING_NL;
                break;
            case Constants.LANG_NO:
                strDispMeaningFldName = Constants.FLD_MEANING_NO;
                break;
            case Constants.LANG_PL:
                strDispMeaningFldName = Constants.FLD_MEANING_PL;
                break;
            case Constants.LANG_PT:
                strDispMeaningFldName = Constants.FLD_MEANING_PT;
                break;
            case Constants.LANG_RO:
                strDispMeaningFldName = Constants.FLD_MEANING_RO;
                break;
            case Constants.LANG_RU:
                strDispMeaningFldName = Constants.FLD_MEANING_RU;
                break;
            case Constants.LANG_SK:
                strDispMeaningFldName = Constants.FLD_MEANING_SK;
                break;

            case Constants.LANG_SV:
                strDispMeaningFldName = Constants.FLD_MEANING_SV;
                break;
            case Constants.LANG_TH:
                strDispMeaningFldName = Constants.FLD_MEANING_TH;
                break;
            case Constants.LANG_TR:
                strDispMeaningFldName = Constants.FLD_MEANING_TR;
                break;
            case Constants.LANG_UK:
                strDispMeaningFldName = Constants.FLD_MEANING_UK;
                break;
            case Constants.LANG_VI:
                strDispMeaningFldName = Constants.FLD_MEANING_VI;
                break;
        }
        return strDispMeaningFldName;
    }

    public String getUserDispMeaningLangDetailedFldName(Integer langMeaningCode) {
//        logger.info("langMeaningCode : " + langMeaningCode);
        String strDispMeaningFldName = Constants.FLD_MEANING_NL_DETAILED;
        if (langMeaningCode == Constants.LANGCODE_AR) {
            strDispMeaningFldName = Constants.FLD_MEANING_AR_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_BN) {
            strDispMeaningFldName = Constants.FLD_MEANING_BN_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_CH_S) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_S_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_CH_T) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_T_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_CS) {
            strDispMeaningFldName = Constants.FLD_MEANING_CS_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_DA) {
            strDispMeaningFldName = Constants.FLD_MEANING_DA_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_DE) {
            strDispMeaningFldName = Constants.FLD_MEANING_DE_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_EL) {
            strDispMeaningFldName = Constants.FLD_MEANING_EL_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_EN) {
            strDispMeaningFldName = Constants.FLD_MEANING_ENG_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_ES) {
            strDispMeaningFldName = Constants.FLD_MEANING_ES_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_FI) {
            strDispMeaningFldName = Constants.FLD_MEANING_FI_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_FR) {
            strDispMeaningFldName = Constants.FLD_MEANING_FR_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_HE) {
            strDispMeaningFldName = Constants.FLD_MEANING_HE_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_HI) {
            strDispMeaningFldName = Constants.FLD_MEANING_HI_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_HR) {
            strDispMeaningFldName = Constants.FLD_MEANING_HR_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_HU) {
            strDispMeaningFldName = Constants.FLD_MEANING_HU_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_ID) {
            strDispMeaningFldName = Constants.FLD_MEANING_ID_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_IT) {
            strDispMeaningFldName = Constants.FLD_MEANING_IT_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_JP) {
            strDispMeaningFldName = Constants.FLD_MEANING_JP_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_KO) {
            strDispMeaningFldName = Constants.FLD_MEANING_KO_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_NL) {
            strDispMeaningFldName = Constants.FLD_MEANING_NL_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_NO) {
            strDispMeaningFldName = Constants.FLD_MEANING_NO_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_PL) {
            strDispMeaningFldName = Constants.FLD_MEANING_PL_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_PT) {
            strDispMeaningFldName = Constants.FLD_MEANING_PT_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_RO) {
            strDispMeaningFldName = Constants.FLD_MEANING_RO_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_RU) {
            strDispMeaningFldName = Constants.FLD_MEANING_RU_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_SK) {
            strDispMeaningFldName = Constants.FLD_MEANING_SK_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_SV) {
            strDispMeaningFldName = Constants.FLD_MEANING_SV_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_TH) {
            strDispMeaningFldName = Constants.FLD_MEANING_TH_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_TR) {
            strDispMeaningFldName = Constants.FLD_MEANING_TR_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_UK) {
            strDispMeaningFldName = Constants.FLD_MEANING_UK_DETAILED;
        } else if (langMeaningCode == Constants.LANGCODE_VI) {
            strDispMeaningFldName = Constants.FLD_MEANING_VI_DETAILED;
        }

        return strDispMeaningFldName;
    }

    public String getUserDispMeaningLangDetailedFldName(String dispMeaningLang) {
        return convertFldLangEngToDetaildedFldName(dispMeaningLang);
    }

    public String convertFldLangEngToDetaildedFldName(String langNameInEnglish) {
//        logger.info("langNameInEnglish : " + langNameInEnglish);
        String strDispMeaningFldName =  Constants.FLD_MEANING_KO_DETAILED;
        switch (langNameInEnglish.toUpperCase()) {
            case Constants.LANG_AR:
                strDispMeaningFldName = Constants.FLD_MEANING_AR_DETAILED;
                break;
            case Constants.LANG_BN:
                strDispMeaningFldName = Constants.FLD_MEANING_BN_DETAILED;
                break;
            case Constants.LANG_CH_S:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_S_DETAILED;
                break;
            case Constants.LANG_CH_T:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_T_DETAILED;
                break;
            case Constants.LANG_CS:
                strDispMeaningFldName = Constants.FLD_MEANING_CS_DETAILED;
                break;
            case Constants.LANG_DA:
                strDispMeaningFldName = Constants.FLD_MEANING_DA_DETAILED;
                break;
            case Constants.LANG_DE:
                strDispMeaningFldName = Constants.FLD_MEANING_DE_DETAILED;
                break;
            case Constants.LANG_EL:
                strDispMeaningFldName = Constants.FLD_MEANING_EL_DETAILED;
                break;
            case Constants.LANG_EN:
                strDispMeaningFldName = Constants.FLD_MEANING_ENG_DETAILED;
                break;
            case Constants.LANG_ES:
                strDispMeaningFldName = Constants.FLD_MEANING_ES_DETAILED;
                break;
            case Constants.LANG_FI:
                strDispMeaningFldName = Constants.FLD_MEANING_FI_DETAILED;
                break;
            case Constants.LANG_FR:
                strDispMeaningFldName = Constants.FLD_MEANING_FR_DETAILED;
                break;
            case Constants.LANG_HE:
                strDispMeaningFldName = Constants.FLD_MEANING_HE_DETAILED;
                break;
            case Constants.LANG_HI:
                strDispMeaningFldName = Constants.FLD_MEANING_HI_DETAILED;
                break;
            case Constants.LANG_HR:
                strDispMeaningFldName = Constants.FLD_MEANING_HR_DETAILED;
                break;
            case Constants.LANG_HU:
                strDispMeaningFldName = Constants.FLD_MEANING_HU_DETAILED;
                break;
            case Constants.LANG_ID:
                strDispMeaningFldName = Constants.FLD_MEANING_ID_DETAILED;
                break;
            case Constants.LANG_IT:
                strDispMeaningFldName = Constants.FLD_MEANING_IT_DETAILED;
                break;
            case Constants.LANG_JP:
                strDispMeaningFldName = Constants.FLD_MEANING_JP_DETAILED;
                break;
            case Constants.LANG_KO:
                strDispMeaningFldName = Constants.FLD_MEANING_KO_DETAILED;
                break;
            case Constants.LANG_NL:
                strDispMeaningFldName = Constants.FLD_MEANING_NL_DETAILED;
                break;
            case Constants.LANG_NO:
                strDispMeaningFldName = Constants.FLD_MEANING_NO_DETAILED;
                break;
            case Constants.LANG_PL:
                strDispMeaningFldName = Constants.FLD_MEANING_PL_DETAILED;
                break;
            case Constants.LANG_PT:
                strDispMeaningFldName = Constants.FLD_MEANING_PT_DETAILED;
                break;
            case Constants.LANG_RO:
                strDispMeaningFldName = Constants.FLD_MEANING_RO_DETAILED;
                break;
            case Constants.LANG_RU:
                strDispMeaningFldName = Constants.FLD_MEANING_RU_DETAILED;
                break;
            case Constants.LANG_SK:
                strDispMeaningFldName = Constants.FLD_MEANING_SK_DETAILED;
                break;
            case Constants.LANG_SV:
                strDispMeaningFldName = Constants.FLD_MEANING_SV_DETAILED;
                break;
            case Constants.LANG_TH:
                strDispMeaningFldName = Constants.FLD_MEANING_TH_DETAILED;
                break;
            case Constants.LANG_TR:
                strDispMeaningFldName = Constants.FLD_MEANING_TR_DETAILED;
                break;
            case Constants.LANG_UK:
                strDispMeaningFldName = Constants.FLD_MEANING_UK_DETAILED;
                break;
            case Constants.LANG_VI:
                strDispMeaningFldName = Constants.FLD_MEANING_VI_DETAILED;
                break;
        }
        return strDispMeaningFldName;
    }
    public String getUserDispMeaningLangTTSFldName(String dispMeaningLang) {
        return convertFldLangEngToLongFldName(dispMeaningLang);
    }
    public String convertFldLangEngToLongFldName(String langNameInEnglish) {
//        logger.info("langNameInEnglish : " + langNameInEnglish);
        String strDispMeaningFldName = Constants.FLD_MEANING_KO_TTS;
        switch (langNameInEnglish.toUpperCase()) {
            case Constants.LANG_AR:
                strDispMeaningFldName = Constants.FLD_MEANING_AR_TTS;
                break;
            case Constants.LANG_BN:
                strDispMeaningFldName = Constants.FLD_MEANING_BN_TTS;
                break;
            case Constants.LANG_CH_S:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_S_TTS;
                break;
            case Constants.LANG_CH_T:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_T_TTS;
                break;
            case Constants.LANG_CS:
                strDispMeaningFldName = Constants.FLD_MEANING_CS_TTS;
                break;
            case Constants.LANG_DA:
                strDispMeaningFldName = Constants.FLD_MEANING_DA_TTS;
                break;
            case Constants.LANG_DE:
                strDispMeaningFldName = Constants.FLD_MEANING_DE_TTS;
                break;
            case Constants.LANG_EL:
                strDispMeaningFldName = Constants.FLD_MEANING_EL_TTS;
                break;
            case Constants.LANG_EN:
                strDispMeaningFldName = Constants.FLD_MEANING_ENG_TTS;
                break;
            case Constants.LANG_ES:
                strDispMeaningFldName = Constants.FLD_MEANING_ES_TTS;
                break;
            case Constants.LANG_FI:
                strDispMeaningFldName = Constants.FLD_MEANING_FI_TTS;
                break;
            case Constants.LANG_FR:
                strDispMeaningFldName = Constants.FLD_MEANING_FR_TTS;
                break;
            case Constants.LANG_HE:
                strDispMeaningFldName = Constants.FLD_MEANING_HE_TTS;
                break;
            case Constants.LANG_HI:
                strDispMeaningFldName = Constants.FLD_MEANING_HI_TTS;
                break;
            case Constants.LANG_HR:
                strDispMeaningFldName = Constants.FLD_MEANING_HR_TTS;
                break;
            case Constants.LANG_HU:
                strDispMeaningFldName = Constants.FLD_MEANING_HU_TTS;
                break;
            case Constants.LANG_ID:
                strDispMeaningFldName = Constants.FLD_MEANING_ID_TTS;
                break;
            case Constants.LANG_IT:
                strDispMeaningFldName = Constants.FLD_MEANING_IT_TTS;
                break;
            case Constants.LANG_JP:
                strDispMeaningFldName = Constants.FLD_MEANING_JP_TTS;
                break;
            case Constants.LANG_KO:
                strDispMeaningFldName = Constants.FLD_MEANING_KO_TTS;
                break;
            case Constants.LANG_NL:
                strDispMeaningFldName = Constants.FLD_MEANING_NL_TTS;
                break;
            case Constants.LANG_NO:
                strDispMeaningFldName = Constants.FLD_MEANING_NO_TTS;
                break;
            case Constants.LANG_PL:
                strDispMeaningFldName = Constants.FLD_MEANING_PL_TTS;
                break;
            case Constants.LANG_PT:
                strDispMeaningFldName = Constants.FLD_MEANING_PT_TTS;
                break;
            case Constants.LANG_RO:
                strDispMeaningFldName = Constants.FLD_MEANING_RO_TTS;
                break;
            case Constants.LANG_RU:
                strDispMeaningFldName = Constants.FLD_MEANING_RU_TTS;
                break;
            case Constants.LANG_SK:
                strDispMeaningFldName = Constants.FLD_MEANING_SK_TTS;
                break;
            case Constants.LANG_SV:
                strDispMeaningFldName = Constants.FLD_MEANING_SV_TTS;
                break;
            case Constants.LANG_TH:
                strDispMeaningFldName = Constants.FLD_MEANING_TH_TTS;
                break;
            case Constants.LANG_TR:
                strDispMeaningFldName = Constants.FLD_MEANING_TR_TTS;
                break;
            case Constants.LANG_UK:
                strDispMeaningFldName = Constants.FLD_MEANING_UK_TTS;
                break;
            case Constants.LANG_VI:
                strDispMeaningFldName = Constants.FLD_MEANING_VI_TTS;
                break;
        }
        return strDispMeaningFldName;
    }
    public String getUserDispMeaningLangTTSFldName(Integer langMeaningCode) {
//        logger.info("langMeaningCode : " + langMeaningCode);
        String strDispMeaningFldName = Constants.FLD_MEANING_KO_TTS;
        if (langMeaningCode == Constants.LANGCODE_AR) {
            strDispMeaningFldName = Constants.FLD_MEANING_AR_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_BN) {
            strDispMeaningFldName = Constants.FLD_MEANING_BN_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_CH_S) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_S_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_CH_T) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_T_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_CS) {
            strDispMeaningFldName = Constants.FLD_MEANING_CS_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_DA) {
            strDispMeaningFldName = Constants.FLD_MEANING_DA_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_DE) {
            strDispMeaningFldName = Constants.FLD_MEANING_DE_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_EL) {
            strDispMeaningFldName = Constants.FLD_MEANING_EL_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_EN) {
            strDispMeaningFldName = Constants.FLD_MEANING_ENG_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_ES) {
            strDispMeaningFldName = Constants.FLD_MEANING_ES_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_FI) {
            strDispMeaningFldName = Constants.FLD_MEANING_FI_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_FR) {
            strDispMeaningFldName = Constants.FLD_MEANING_FR_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_HE) {
            strDispMeaningFldName = Constants.FLD_MEANING_HE_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_HI) {
            strDispMeaningFldName = Constants.FLD_MEANING_HI_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_HR) {
            strDispMeaningFldName = Constants.FLD_MEANING_HR_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_HU) {
            strDispMeaningFldName = Constants.FLD_MEANING_HU_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_ID) {
            strDispMeaningFldName = Constants.FLD_MEANING_ID_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_IT) {
            strDispMeaningFldName = Constants.FLD_MEANING_IT_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_JP) {
            strDispMeaningFldName = Constants.FLD_MEANING_JP_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_KO) {
            strDispMeaningFldName = Constants.FLD_MEANING_KO_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_NL) {
            strDispMeaningFldName = Constants.FLD_MEANING_NL_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_NO) {
            strDispMeaningFldName = Constants.FLD_MEANING_NO_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_PL) {
            strDispMeaningFldName = Constants.FLD_MEANING_PL_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_PT) {
            strDispMeaningFldName = Constants.FLD_MEANING_PT_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_RO) {
            strDispMeaningFldName = Constants.FLD_MEANING_RO_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_RU) {
            strDispMeaningFldName = Constants.FLD_MEANING_RU_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_SK) {
            strDispMeaningFldName = Constants.FLD_MEANING_SK_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_SV) {
            strDispMeaningFldName = Constants.FLD_MEANING_SV_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_TH) {
            strDispMeaningFldName = Constants.FLD_MEANING_TH_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_TR) {
            strDispMeaningFldName = Constants.FLD_MEANING_TR_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_UK) {
            strDispMeaningFldName = Constants.FLD_MEANING_UK_TTS;
        } else if (langMeaningCode == Constants.LANGCODE_VI) {
            strDispMeaningFldName = Constants.FLD_MEANING_VI_TTS;
        }

        return strDispMeaningFldName;
    }


    public String getUserDispMeaningLangFldName(Integer langMeaningCode) {
//        logger.info("langMeaningCode : " + langMeaningCode);
        String strDispMeaningFldName = Constants.FLD_MEANING_KO;
        if (langMeaningCode == Constants.LANGCODE_AR) {
            strDispMeaningFldName = Constants.FLD_MEANING_AR;
        } else if (langMeaningCode == Constants.LANGCODE_BN) {
            strDispMeaningFldName = Constants.FLD_MEANING_BN;
        } else if (langMeaningCode == Constants.LANGCODE_CH_S) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_S;
        } else if (langMeaningCode == Constants.LANGCODE_CH_T) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_T;
        } else if (langMeaningCode == Constants.LANGCODE_CS) {
            strDispMeaningFldName = Constants.FLD_MEANING_CS;
        } else if (langMeaningCode == Constants.LANGCODE_DA) {
            strDispMeaningFldName = Constants.FLD_MEANING_DA;
        } else if (langMeaningCode == Constants.LANGCODE_DE) {
            strDispMeaningFldName = Constants.FLD_MEANING_DE;
        } else if (langMeaningCode == Constants.LANGCODE_EL) {
            strDispMeaningFldName = Constants.FLD_MEANING_EL;
        } else if (langMeaningCode == Constants.LANGCODE_EN) {
            strDispMeaningFldName = Constants.FLD_MEANING_ENG;
        } else if (langMeaningCode == Constants.LANGCODE_ES) {
            strDispMeaningFldName = Constants.FLD_MEANING_ES;
        } else if (langMeaningCode == Constants.LANGCODE_FI) {
            strDispMeaningFldName = Constants.FLD_MEANING_FI;
        } else if (langMeaningCode == Constants.LANGCODE_FR) {
            strDispMeaningFldName = Constants.FLD_MEANING_FR;
        } else if (langMeaningCode == Constants.LANGCODE_HE) {
            strDispMeaningFldName = Constants.FLD_MEANING_HE;
        } else if (langMeaningCode == Constants.LANGCODE_HI) {
            strDispMeaningFldName = Constants.FLD_MEANING_HI;
        } else if (langMeaningCode == Constants.LANGCODE_HR) {
            strDispMeaningFldName = Constants.FLD_MEANING_HR;
        } else if (langMeaningCode == Constants.LANGCODE_HU) {
            strDispMeaningFldName = Constants.FLD_MEANING_HU;
        } else if (langMeaningCode == Constants.LANGCODE_ID) {
            strDispMeaningFldName = Constants.FLD_MEANING_ID;
        } else if (langMeaningCode == Constants.LANGCODE_IT) {
            strDispMeaningFldName = Constants.FLD_MEANING_IT;
        } else if (langMeaningCode == Constants.LANGCODE_JP) {
            strDispMeaningFldName = Constants.FLD_MEANING_JP;
        } else if (langMeaningCode == Constants.LANGCODE_KO) {
            strDispMeaningFldName = Constants.FLD_MEANING_KO;
        } else if (langMeaningCode == Constants.LANGCODE_NL) {
            strDispMeaningFldName = Constants.FLD_MEANING_NL;
        } else if (langMeaningCode == Constants.LANGCODE_NO) {
            strDispMeaningFldName = Constants.FLD_MEANING_NO;
        } else if (langMeaningCode == Constants.LANGCODE_PL) {
            strDispMeaningFldName = Constants.FLD_MEANING_PL;
        } else if (langMeaningCode == Constants.LANGCODE_PT) {
            strDispMeaningFldName = Constants.FLD_MEANING_PT;
        } else if (langMeaningCode == Constants.LANGCODE_RO) {
            strDispMeaningFldName = Constants.FLD_MEANING_RO;
        } else if (langMeaningCode == Constants.LANGCODE_RU) {
            strDispMeaningFldName = Constants.FLD_MEANING_RU;
        } else if (langMeaningCode == Constants.LANGCODE_SK) {
            strDispMeaningFldName = Constants.FLD_MEANING_SK;
        } else if (langMeaningCode == Constants.LANGCODE_SV) {
            strDispMeaningFldName = Constants.FLD_MEANING_SV;
        } else if (langMeaningCode == Constants.LANGCODE_TH) {
            strDispMeaningFldName = Constants.FLD_MEANING_TH;
        } else if (langMeaningCode == Constants.LANGCODE_TR) {
            strDispMeaningFldName = Constants.FLD_MEANING_TR;
        } else if (langMeaningCode == Constants.LANGCODE_UK) {
            strDispMeaningFldName = Constants.FLD_MEANING_UK;
        } else if (langMeaningCode == Constants.LANGCODE_VI) {
            strDispMeaningFldName = Constants.FLD_MEANING_VI;
        }
        return strDispMeaningFldName;
    }
    public String getUserDispMeaningLangForHideAllFldName(String langNameInEnglish) {
//        logger.info("langNameInEnglish : " + langNameInEnglish);
        String strDispMeaningFldName = Constants.FLD_MEANING_KO_FOR_HIDE_ALL;
        switch (langNameInEnglish.toUpperCase()) {
            case Constants.LANG_AR:
                strDispMeaningFldName = Constants.FLD_MEANING_AR_FOR_HIDE_ALL;
                break;
            case Constants.LANG_BN:
                strDispMeaningFldName = Constants.FLD_MEANING_BN_FOR_HIDE_ALL;
                break;
            case Constants.LANG_CH_S:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_S_FOR_HIDE_ALL;
                break;
            case Constants.LANG_CH_T:
                strDispMeaningFldName = Constants.FLD_MEANING_CH_T_FOR_HIDE_ALL;
                break;
            case Constants.LANG_CS:
                strDispMeaningFldName = Constants.FLD_MEANING_CS_FOR_HIDE_ALL;
                break;
            case Constants.LANG_DA:
                strDispMeaningFldName = Constants.FLD_MEANING_DA_FOR_HIDE_ALL;
                break;
            case Constants.LANG_DE:
                strDispMeaningFldName = Constants.FLD_MEANING_DE_FOR_HIDE_ALL;
                break;
            case Constants.LANG_EL:
                strDispMeaningFldName = Constants.FLD_MEANING_EL_FOR_HIDE_ALL;
                break;
            case Constants.LANG_EN:
                strDispMeaningFldName = Constants.FLD_MEANING_ENG_FOR_HIDE_ALL;
                break;
            case Constants.LANG_ES:
                strDispMeaningFldName = Constants.FLD_MEANING_ES_FOR_HIDE_ALL;
                break;
            case Constants.LANG_FI:
                strDispMeaningFldName = Constants.FLD_MEANING_FI_FOR_HIDE_ALL;
                break;
            case Constants.LANG_FR:
                strDispMeaningFldName = Constants.FLD_MEANING_FR_FOR_HIDE_ALL;
                break;
            case Constants.LANG_HE:
                strDispMeaningFldName = Constants.FLD_MEANING_HE_FOR_HIDE_ALL;
                break;
            case Constants.LANG_HI:
                strDispMeaningFldName = Constants.FLD_MEANING_HI_FOR_HIDE_ALL;
                break;
            case Constants.LANG_HR:
                strDispMeaningFldName = Constants.FLD_MEANING_HR_FOR_HIDE_ALL;
                break;
            case Constants.LANG_HU:
                strDispMeaningFldName = Constants.FLD_MEANING_HU_FOR_HIDE_ALL;
                break;
            case Constants.LANG_ID:
                strDispMeaningFldName = Constants.FLD_MEANING_ID_FOR_HIDE_ALL;
                break;
            case Constants.LANG_IT:
                strDispMeaningFldName = Constants.FLD_MEANING_IT_FOR_HIDE_ALL;
                break;
            case Constants.LANG_JP:
                strDispMeaningFldName = Constants.FLD_MEANING_JP_FOR_HIDE_ALL;
                break;
            case Constants.LANG_KO:
                strDispMeaningFldName = Constants.FLD_MEANING_KO_FOR_HIDE_ALL;
                break;
            case Constants.LANG_NL:
                strDispMeaningFldName = Constants.FLD_MEANING_NL_FOR_HIDE_ALL;
                break;
            case Constants.LANG_NO:
                strDispMeaningFldName = Constants.FLD_MEANING_NO_FOR_HIDE_ALL;
                break;
            case Constants.LANG_PL:
                strDispMeaningFldName = Constants.FLD_MEANING_PL_FOR_HIDE_ALL;
                break;
            case Constants.LANG_PT:
                strDispMeaningFldName = Constants.FLD_MEANING_PT_FOR_HIDE_ALL;
                break;
            case Constants.LANG_RO:
                strDispMeaningFldName = Constants.FLD_MEANING_RO_FOR_HIDE_ALL;
                break;
            case Constants.LANG_RU:
                strDispMeaningFldName = Constants.FLD_MEANING_RU_FOR_HIDE_ALL;
                break;
            case Constants.LANG_SK:
                strDispMeaningFldName = Constants.FLD_MEANING_SK_FOR_HIDE_ALL;
                break;
            case Constants.LANG_SV:
                strDispMeaningFldName = Constants.FLD_MEANING_SV_FOR_HIDE_ALL;
                break;
            case Constants.LANG_TH:
                strDispMeaningFldName = Constants.FLD_MEANING_TH_FOR_HIDE_ALL;
                break;
            case Constants.LANG_TR:
                strDispMeaningFldName = Constants.FLD_MEANING_TR_FOR_HIDE_ALL;
                break;
            case Constants.LANG_UK:
                strDispMeaningFldName = Constants.FLD_MEANING_UK_FOR_HIDE_ALL;
                break;
            case Constants.LANG_VI:
                strDispMeaningFldName = Constants.FLD_MEANING_VI_FOR_HIDE_ALL;
                break;
        }
        return strDispMeaningFldName;
    }

    public String getUserDispMeaningLangForHideAllFldName(Integer langMeangingCode) {
//        logger.info("langMeangingCode : " + langMeangingCode);
        String strDispMeaningFldName = Constants.FLD_MEANING_KO_FOR_HIDE_ALL;
        if (langMeangingCode == Constants.LANGCODE_AR) {
            strDispMeaningFldName = Constants.FLD_MEANING_AR_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_BN) {
            strDispMeaningFldName = Constants.FLD_MEANING_BN_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_CH_S) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_S_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_CH_T) {
            strDispMeaningFldName = Constants.FLD_MEANING_CH_T_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_CS) {
            strDispMeaningFldName = Constants.FLD_MEANING_CS_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_DA) {
            strDispMeaningFldName = Constants.FLD_MEANING_DA_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_DE) {
            strDispMeaningFldName = Constants.FLD_MEANING_DE_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_EL) {
            strDispMeaningFldName = Constants.FLD_MEANING_EL_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_EN) {
            strDispMeaningFldName = Constants.FLD_MEANING_ENG_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_ES) {
            strDispMeaningFldName = Constants.FLD_MEANING_ES_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_FI) {
            strDispMeaningFldName = Constants.FLD_MEANING_FI_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_FR) {
            strDispMeaningFldName = Constants.FLD_MEANING_FR_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_HE) {
            strDispMeaningFldName = Constants.FLD_MEANING_HE_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_HI) {
            strDispMeaningFldName = Constants.FLD_MEANING_HI_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_HR) {
            strDispMeaningFldName = Constants.FLD_MEANING_HR_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_HU) {
            strDispMeaningFldName = Constants.FLD_MEANING_HU_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_ID) {
            strDispMeaningFldName = Constants.FLD_MEANING_ID_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_IT) {
            strDispMeaningFldName = Constants.FLD_MEANING_IT_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_JP) {
            strDispMeaningFldName = Constants.FLD_MEANING_JP_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_KO) {
            strDispMeaningFldName = Constants.FLD_MEANING_KO_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_NL) {
            strDispMeaningFldName = Constants.FLD_MEANING_NL_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_NO) {
            strDispMeaningFldName = Constants.FLD_MEANING_NO_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_PL) {
            strDispMeaningFldName = Constants.FLD_MEANING_PL_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_PT) {
            strDispMeaningFldName = Constants.FLD_MEANING_PT_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_RO) {
            strDispMeaningFldName = Constants.FLD_MEANING_RO_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_RU) {
            strDispMeaningFldName = Constants.FLD_MEANING_RU_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_SK) {
            strDispMeaningFldName = Constants.FLD_MEANING_SK_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_SV) {
            strDispMeaningFldName = Constants.FLD_MEANING_SV_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_TH) {
            strDispMeaningFldName = Constants.FLD_MEANING_TH_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_TR) {
            strDispMeaningFldName = Constants.FLD_MEANING_TR_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_UK) {
            strDispMeaningFldName = Constants.FLD_MEANING_UK_FOR_HIDE_ALL;
        } else if (langMeangingCode == Constants.LANGCODE_VI) {
            strDispMeaningFldName = Constants.FLD_MEANING_VI_FOR_HIDE_ALL;
        }
        return strDispMeaningFldName;
    }
    public void addFldMeaningIntoMap (HashMap<String, Object> map, String langMeaning) {
        addFldMeaningIntoMap(map, convertLangNameToLangCode(langMeaning));
    }
    public void addFldMeaningIntoMap (HashMap<String, Object> map, Integer langMeaningCode) {
        String fld_Meaning = getUserDispMeaningLangFldName(langMeaningCode);
        String fld_Meaning_Detailed = getUserDispMeaningLangDetailedFldName(langMeaningCode);
        String fld_Meaning_TTS = getUserDispMeaningLangTTSFldName(langMeaningCode);
        String fld_Meaning_For_Hide_all = getUserDispMeaningLangForHideAllFldName(langMeaningCode);


        map.put("fld_Meaning", fld_Meaning);
        map.put("fld_Meaning_Detailed", fld_Meaning_Detailed);
        map.put("fld_Meaning_TTS", fld_Meaning_TTS);
        map.put("fld_Meaning_For_Hide_all", fld_Meaning_For_Hide_all);


    }

    //이걸 안하면 Mac에서 글자가 작고 파란색으로 나온다.
    private String normalizeInputText(String strOne) {
        String strOutput = strOne;
        if (strOne.contains("↵")) {
            strOutput = strOne.replaceAll("↵", "\r\n");
        }
        return strOutput;
    }

    private void setMeaningInDialogDTO(DTO_INPUT_MAKE_RUBY dto, DTO_DIALOGUE dtoDialogue, VO_DIC_COMMON voWithoutPunct) {
        if ((dto.getTRANSLATE_INPUT_TEXT() == Constants.IS_YES)
                && (dtoDialogue.getMEANING().equals(""))
                && (!(voWithoutPunct.getMEANING().equals("")))) {
            dtoDialogue.setMEANING(voWithoutPunct.getMEANING());
        }
    }

    private void setVocaIdAndVocaIdToSendServerInDialogDTO(DTO_DIALOGUE dtoDialogue, Integer vocaType, Integer vocaId) {
        dtoDialogue.setVOCA_TYPE(vocaType);
        dtoDialogue.setVOCA_ID(vocaId);
        dtoDialogue.setVOCA_ID_TO_SEND_SERVER(vocaId);
    }

    private void setBaseVocaTypeAndVocaIdInDialogDTO(DTO_SUBTITLE_PARSED dtoSubtitleParsed, DTO_DIALOGUE dtoDialogue, Integer vocaType, Integer vocaId) {
        dtoDialogue.setBASE_VOCA_TYPE(vocaType);
        dtoDialogue.setBASE_VOCA_ID(vocaId);

        Map<String, DTO_DIALOGUE> mapBaseVoca = dtoSubtitleParsed.getMAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY();
        mapBaseVoca.put(vocaId + "_" + vocaType, dtoDialogue);
        dtoSubtitleParsed.setMAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY(mapBaseVoca);

//		dtoDialogue.setBASE_VOCA_ID_TO_SEND_SERVER(vocaId);
    }

    private void makeMapDialogueInforbyVocaTypeIDKey(DTO_SUBTITLE_PARSED dtoSubtitleParsed, DTO_DIALOGUE dtoDialogue) {
        //MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY 으로 다시 넣어준다. bookmark등을 넣을때 활용할려고 하는거다.
        String vocaID_Type = dtoDialogue.getVOCA_ID() + Constants.UNDERSCORE + dtoDialogue.getVOCA_TYPE();
        Map<String, DTO_DIALOGUE> MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY = dtoSubtitleParsed.getMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY();
        MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY.put(vocaID_Type, dtoDialogue);
        dtoSubtitleParsed.setMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY(MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY);
    }

    //자막에 대해서 서버에 저장된 KNOW자막이 있으면 그 값을 준다. 북마크도 리턴해준다. 숨긴자막은 서버에서 처리하지 않는다.
    private void getSubtitleKnowAndBookmarkDTO(DTO_SUBTITLE_PARSED dtoSubtitleParsed) {
        try {


            List<DTO_VOCA_TYPE_ID> listVocaTypeID = new ArrayList<DTO_VOCA_TYPE_ID>();
            List<DTO_VOCA_TYPE_ID> listBaseVocaTypeID = new ArrayList<DTO_VOCA_TYPE_ID>();
            for(DTO_DIALOGUE dtoDialogue : dtoSubtitleParsed.getLIST_DIALOGUE_INFO()) {
                //listVocaTypeID은 전부 넣는다. 3, 4의 값밖에 없기 때문이다.
                listVocaTypeID.add(new DTO_VOCA_TYPE_ID.Builder()
                        .VOCA_TYPE(dtoDialogue.getVOCA_TYPE())
                        .VOCA_ID(dtoDialogue.getVOCA_ID())
                        .build());

                if (dtoDialogue.getBASE_VOCA_TYPE().equals(Constants.VOCA_TYPE_WORD)
                        || dtoDialogue.getBASE_VOCA_TYPE().equals(Constants.VOCA_TYPE_SENTENCE)) {
                    listBaseVocaTypeID.add(new DTO_VOCA_TYPE_ID.Builder()
                            .VOCA_TYPE(dtoDialogue.getBASE_VOCA_TYPE())
                            .VOCA_ID(dtoDialogue.getBASE_VOCA_ID())
                            .build());
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    protected String getTbl_DIC_BOOKMARK() {
        return Constants.DIC_ENG_BOOKMARK;
    }

    protected void insertRubyTextInSqliteDTO(DTO_OUTPUT_RUBY_TEXT dtoOutputRubyText, DTO_SUBTITLE_PARSED dtoSubtitleParsed, List<String> LIST_INPUT_TEXT) {

        try {
            Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWords = dtoOutputRubyText.getVOCA_LIST();
            List<String> listRubyText = dtoOutputRubyText.getLIST_RUBY_TEXT();
            //이건 현재 Ruby Tag에서 Voca를 지웠기 때문에
            LinkedHashMap<Integer, Integer> mapWordAppearanceOrder = getWordAppearanceOrder(listRubyText);
            Map<String, DTO_DIALOGUE> mapSubtitleInfo = dtoSubtitleParsed.getMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY();
            List<DTO_DIALOGUE> listDialogue = dtoSubtitleParsed.getLIST_DIALOGUE_INFO();
            
            DLog.i("SUBTITLE_ANALYSIS", "4. DB 저장 시작");
            long startTime = System.currentTimeMillis();
            
//            if (this instanceof MOVIE_SQLITEService) {
                subDatabase.deleteAllRecordsInSubtitleDb();
//            }
            
            long step1Start = System.currentTimeMillis();
            insertSubtitleTableInSubtitleDb(listRubyText, listDialogue);
            long step1Time = System.currentTimeMillis() - step1Start;
            DLog.i("SUBTITLE_ANALYSIS", "4-1. SUBTITLE 테이블 저장 완료: " + step1Time + "ms");
            
            long step2Start = System.currentTimeMillis();
            insertWordInDicTableInSubtitleDb(mapUniqueWords);
            long step2Time = System.currentTimeMillis() - step2Start;
            DLog.i("SUBTITLE_ANALYSIS", "4-2. DIC 테이블 저장 완료: " + step2Time + "ms");
            
            long step3Start = System.currentTimeMillis();
            insertSubtitleWordListTableInSubtitleDb(listRubyText);
            long step3Time = System.currentTimeMillis() - step3Start;
            DLog.i("SUBTITLE_ANALYSIS", "4-3. SUBTITLE_WORDLIST 테이블 저장 완료: " + step3Time + "ms");
            
            long totalTime = System.currentTimeMillis() - startTime;
            DLog.i("SUBTITLE_ANALYSIS", "4. DB 저장 완료: " + totalTime + "ms");
            
//            //각 대사별 존재하는 단어리스트를 넣어준다.
//            JDBC4PreparedStatement preparedStatementSubtitleWordlist = insertToSubtitleWordlistTblDTO(conn, listRubyText);
//            int[] insertedSubtitleWordlist = preparedStatementSubtitleWordlist.executeBatch();
//            preparedStatementSubtitleWordlist.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    private void insertSubtitleWordListTableInSubtitleDb(List<String> listRubyText) {
        int id = 0;
        Integer subtitleId = 0;
        List<SubtitleWordListModel> batchList = new ArrayList<>();
        
        for (String rubyText : listRubyText) {
            Map<Integer, Object> mapSubtitleWordList = getSubtitleWordListDTO(rubyText);
            List<Integer> vocaIDList = (List<Integer>)mapSubtitleWordList.get(1);
            List<Integer> vocaTypeList = (List<Integer>)mapSubtitleWordList.get(2);
            if (vocaIDList.size() == vocaTypeList.size()) {
                Map<Integer, Integer> mapWordList = new HashMap<Integer, Integer>();
                for (Integer dispOrder = 0; dispOrder < vocaIDList.size(); dispOrder++) {
                    if (!(mapWordList.containsKey(vocaIDList.get(dispOrder)))) {
                        SubtitleWordListModel model = new SubtitleWordListModel();
                        model.setId(id++);
                        model.setSubtitleId(subtitleId);
                        model.setVocaId(vocaIDList.get(dispOrder));
                        model.setVocaType(vocaTypeList.get(dispOrder));
                        batchList.add(model);

                        //같은 자막에서 중복된 단어는 SUBTITLE_WORDLIST의 하나만 넣을려고 함.
                        mapWordList.put(vocaIDList.get(dispOrder), 0);
                    }
                }
                subtitleId++; //
            }
        }
        
        // 배치 삽입
        if (!batchList.isEmpty()) {
            subDatabase.addItemsInSubtitleWordListInSubtitleDb(batchList);
        }
    }

    private void insertWordInDicTableInSubtitleDb(Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWords) {
        int i = 0;
        List<DicModel> batchList = new ArrayList<>();
        
        for (String strWordWithPos : mapUniqueWords.keySet()) {
            DTO_VOCA_DETAIL_RUBY_TEXT dtoVoca = mapUniqueWords.get(strWordWithPos);
            DicModel dicModel = new DicModel();
            dicModel.setId(i++);
            dicModel.setLangStudy(Constants.LANGCODE_EN);
            dicModel.setVocaType(dtoVoca.getVOCA_TYPE());
            dicModel.setVocaId(dtoVoca.getVOCA_ID());
            dicModel.setVIVoca(dtoVoca.getVOCA());
            dicModel.setVocaDisplay(dtoVoca.getVOCA());
            dicModel.setVocaTTS(dtoVoca.getVOCA_TTS());
            dicModel.setVocaDisplayRuby(StringUtils.replaceNewLineToBRTag(dtoVoca.getVOCA_TTS()));
            dicModel.setMeaning(dtoVoca.getMEANING());
            dicModel.setMeaningEng(dtoVoca.getMEANING_ENG());
            dicModel.setMeaningDetailed(dtoVoca.getMEANING_DETAILED());
            dicModel.setVIPronounce(dtoVoca.getPRONOUNCE());
            dicModel.setVocaKnow(dtoVoca.getVOCA_KNOW());
            dicModel.setVocaKnowPronounce(dtoVoca.getVOCA_KNOWPRONOUNCE());
            dicModel.setBookmark(dtoVoca.getBOOKMARK());
            dicModel.setWordLevel(dtoVoca.getVOCA_LEVEL());
            dicModel.setFrequency(dtoVoca.getFREQUENCY());
            batchList.add(dicModel);
        }
        
        // 배치 삽입
        if (!batchList.isEmpty()) {
            subDatabase.addWordsInDicTableInSubtitleDb(batchList);
        }
    }

    private void insertSubtitleTableInSubtitleDb(List<String> listRubyText, List<DTO_DIALOGUE> listDialogue) {
        List<DicModel> batchList = new ArrayList<>();
        
        for (Integer i = 0; i < listDialogue.size(); i ++) {
            DTO_DIALOGUE dialogue = listDialogue.get(i);
            DicModel dicModel = new DicModel();
            dicModel.setId(i);
            dicModel.setLangStudy(dialogue.getLANG_STUDY_CODE());
            dicModel.setVocaType(dialogue.getVOCA_TYPE());
            dicModel.setVocaTypeBase(dialogue.getBASE_VOCA_TYPE());
            dicModel.setVocaId(i);
            dicModel.setVocaIdBase(dialogue.getBASE_VOCA_ID());
            String vocaDisplayRuby = listRubyText.get(i);
            dicModel.setVocaDisplay(dialogue.getVOCA());
            dicModel.setVocaDisplayRuby(StringUtils.replaceNewLineToBRTag(vocaDisplayRuby));
            dicModel.setMeaning(dialogue.getMEANING());
            dicModel.setVocaKnow(dialogue.getVOCA_KNOW());
            dicModel.setVocaKnowPronounce(dialogue.getVOCA_KNOWPRONOUNCE());
            dicModel.setBookmark(dialogue.getBOOKMARK());
            dicModel.setStartTime(dialogue.getSTART_TIME());
            dicModel.setEndTime(dialogue.getEND_TIME());
            dicModel.setStartTimeOriginal(dialogue.getSTART_TIME_ORIGINAL());
            dicModel.setEndTimeOriginal(dialogue.getEND_TIME_ORIGINAL());

            dicModel.setSubtitleOriginal(dialogue.getVOCA_ORIGINAL());
            dicModel.setUsed(dialogue.getUSED());
            dicModel.setMemo(dialogue.getMEMO());
            dicModel.setVIRepeatCount(dialogue.getREPEAT());

            batchList.add(dicModel);
        }
        
        // 배치 삽입
        if (!batchList.isEmpty()) {
            subDatabase.addSubtitles(batchList);
        }
    }
//
//    private JDBC4PreparedStatement insertToDicTblDTO(Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWords, LinkedHashMap<Integer, Integer> mapWordAppearanceOrder, Connection conn)
//            throws SQLException {
//        String compiledQuery = "INSERT OR IGNORE INTO DIC('ID', 'VOCA', 'VOCA_TTS', 'VOCA_ID', 'PRONOUNCE', 'VOCA_KNOW', 'MEANING', "
//                + "'MEANING_TTS', 'BOOKMARK', 'VOCA_KNOWPRONOUNCE', 'VOCA_LEVEL', 'FREQUENCY', 'VOCA_TYPE', 'HANJA', 'JMDICT_MEANING', 'JMDICT_MEANING_ENG', 'VOCA_APPEARANCE_ORDER', 'MEANING_DETAILED', 'VOCA_ID_TO_SEND_SERVER', 'POSALL', 'MEANING_ENG') "
//                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        JDBC4PreparedStatement preparedStatement = (JDBC4PreparedStatement) conn.prepareStatement(compiledQuery);
////        conn.setAutoCommit(false);
//        Integer id = 0;
//        for (String strWordWithPos : mapUniqueWords.keySet()) {
//            DTO_VOCA_DETAIL_RUBY_TEXT dtoVoca = mapUniqueWords.get(strWordWithPos);
//
//            preparedStatement.setInt(1, id++);
//
//            preparedStatement.setString(2, dtoVoca.getVOCA());
//            preparedStatement.setString(3, dtoVoca.getVOCA_TTS());
//            preparedStatement.setInt(4, dtoVoca.getVOCA_ID());
//            //아래처럼 하면 일본어의 경우 히라가나에 가타가나 발음이 들어가서 보이게 된다.
//            preparedStatement.setString(5, dtoVoca.getPRONOUNCE_BASEFORM().equals("") ? dtoVoca.getPRONOUNCE() : dtoVoca.getPRONOUNCE_BASEFORM());
////        	preparedStatement.setString(5, dtoVoca.getPRONOUNCE());
//            preparedStatement.setInt(6, dtoVoca.getVOCA_KNOW());
//            preparedStatement.setString(7, dtoVoca.getMEANING());
//            preparedStatement.setString(8, dtoVoca.getMEANING_TTS());
//            preparedStatement.setInt(9, dtoVoca.getBOOKMARK());
//            preparedStatement.setInt(10, dtoVoca.getVOCA_KNOWPRONOUNCE());
//            preparedStatement.setInt(11, dtoVoca.getVOCA_LEVEL());
//            preparedStatement.setInt(12, dtoVoca.getFREQUENCY());
//            preparedStatement.setInt(13, dtoVoca.getVOCA_TYPE());
//            preparedStatement.setString(14, dtoVoca.getHANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN());
//
//            preparedStatement.setString(15, dtoVoca.getJMDICT_MEANING());
//            preparedStatement.setString(16, dtoVoca.getJMDICT_MEANING_ENG());
//            if (mapWordAppearanceOrder.containsKey(dtoVoca.getVOCA_ID())) {
//                preparedStatement.setInt(17,  mapWordAppearanceOrder.get(dtoVoca.getVOCA_ID()));
//            } else {
//                preparedStatement.setInt(17,  0);
//            }
//            preparedStatement.setString(18, dtoVoca.getMEANING_DETAILED());
//            preparedStatement.setInt(19, dtoVoca.getVOCA_ID());
//            preparedStatement.setString(20, dtoVoca.getPOS_VOCA());
//            preparedStatement.setString(21, dtoVoca.getMEANING_ENG());
//            preparedStatement.addBatch();
//        }
//        return preparedStatement;
//    }
//
//    private JDBC4PreparedStatement insertToSubtitleTblDTO(DTO_SUBTITLE_PARSED dtoSubtitleParsed, List<String> listRubyText, Connection conn) throws SQLException {
//        String compiledQuery = "INSERT OR IGNORE INTO SUBTITLE('ID', 'LANG_STUDY', 'VOCA', 'VOCA_RUBY', 'START_TIME', 'END_TIME', 'BOOKMARK', 'REPEAT', "
//                + "'START_TIME_ORIGINAL', 'END_TIME_ORIGINAL', 'VOCA_ORIGINAL', 'RECORDING_PATH', 'VOCA_KNOW', 'USED', 'VOCA_TYPE', 'VOCA_ID', "
//                + "'VOCA_KNOWPRONOUNCE', 'MEMO', 'MEANING', 'VOCA_ID_TO_SEND_SERVER', 'BASE_VOCA_TYPE', 'BASE_VOCA_ID') "
//                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        JDBC4PreparedStatement preparedStatement2 = (JDBC4PreparedStatement) conn.prepareStatement(compiledQuery);
////		Map<String, DTO_DIALOGUE> mapDialogue = dtoSubtitleParsed.getMAP_DIALOGUE_INFO();
////		Map<String, DTO_DIALOGUE> mapDialogueWithoutPunct = dtoSubtitleParsed.getMAP_DIALOGUE_WITHOUT_PUNCT_INFO();
//        List<DTO_DIALOGUE> listDialogue = dtoSubtitleParsed.getLIST_DIALOGUE_INFO();
//        Map<String, DTO_DIALOGUE> mapSubtitleInfo = dtoSubtitleParsed.getMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY();
//
//
//
//        for (Integer i = 0; i < listDialogue.size(); i ++) {
//            DTO_DIALOGUE dialogue = listDialogue.get(i);
//
//            String vocaID_Type = dialogue.getVOCA_ID() + Constants.UNDERSCORE + dialogue.getVOCA_TYPE();
//            int vocaKnow = dialogue.getVOCA_KNOW();
//            int vocaKnowPronounce = dialogue.getVOCA_KNOWPRONOUNCE();
//            if (mapSubtitleInfo.containsKey(vocaID_Type)) {
//                DTO_DIALOGUE dtoDialogue = mapSubtitleInfo.get(vocaID_Type);
//                vocaKnow = dtoDialogue.getVOCA_KNOW();
//                vocaKnowPronounce = dtoDialogue.getVOCA_KNOWPRONOUNCE();
//            }
//
//            preparedStatement2.setInt(1, i);
//            preparedStatement2.setInt(2, dialogue.getLANG_STUDY_CODE());
//            preparedStatement2.setString(3, DalString.convertNewlineToSlashN(dialogue.getVOCA()));
//            preparedStatement2.setString(4, listRubyText.get(i));
//            preparedStatement2.setInt(5, dialogue.getSTART_TIME());
//            preparedStatement2.setInt(6, dialogue.getEND_TIME());
//            preparedStatement2.setInt(7, dialogue.getBOOKMARK()); //TODO : 북마크의 정보를 넣어야 한다.
//            preparedStatement2.setInt(8, dialogue.getREPEAT());
//            preparedStatement2.setInt(9, dialogue.getSTART_TIME_ORIGINAL());
//            preparedStatement2.setInt(10, dialogue.getEND_TIME_ORIGINAL());
//            preparedStatement2.setString(11, dialogue.getVOCA_ORIGINAL());
//            preparedStatement2.setString(12, dialogue.getRECORDING_PATH());
//            preparedStatement2.setInt(13, vocaKnow);
//            preparedStatement2.setInt(14, dialogue.getUSED());
//            preparedStatement2.setInt(15, dialogue.getVOCA_TYPE());
//            preparedStatement2.setInt(16, dialogue.getVOCA_ID());
//            preparedStatement2.setInt(17, vocaKnowPronounce);
//            preparedStatement2.setString(18, DalString.convertNewlineToSlashN(dialogue.getMEMO()));
//            preparedStatement2.setString(19, DalString.convertNewlineToSlashN(dialogue.getMEANING()));
//            preparedStatement2.setInt(20, dialogue.getVOCA_ID_TO_SEND_SERVER());
//            preparedStatement2.setInt(21, dialogue.getBASE_VOCA_TYPE());
//            preparedStatement2.setInt(22, dialogue.getBASE_VOCA_ID());
//            preparedStatement2.addBatch();
//        }
//        return preparedStatement2;
//    }
//
//    private JDBC4PreparedStatement insertToMediaInfoTblDTO(Connection conn, DTO_SUBTITLE_PARSED dtoSubtitleParsed) throws SQLException {
//        String compiledQuery = "INSERT OR IGNORE INTO MEDIA_INFO('TITLE', 'TITLE_TTS', 'ARTIST', 'ARTIST_TTS', 'LYRIC_CREATOR', 'LENGTH', 'LYRIC_FILE_CREATOR', 'VERSION', "
//                + "'LYRIC_FILE_EDITOR', 'ALBUM') "
//                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        JDBC4PreparedStatement preparedStatement2 = (JDBC4PreparedStatement) conn.prepareStatement(compiledQuery);
//        MediaInfo mediaInfo = dtoSubtitleParsed.getMEDIAINFO();
//
//        preparedStatement2.setString(1, mediaInfo.getTitle());
//        preparedStatement2.setString(2, mediaInfo.getTitleTts());
//        preparedStatement2.setString(3, mediaInfo.getArtist());
//        preparedStatement2.setString(4, mediaInfo.getArtistTts());
//        preparedStatement2.setString(5, mediaInfo.getLyricCreator());
//        preparedStatement2.setInt(6, mediaInfo.getLength());
//        preparedStatement2.setString(7, mediaInfo.getLyricFileCreator());
//        preparedStatement2.setString(8, mediaInfo.getVersion());
//        preparedStatement2.setString(9, mediaInfo.getLyricFileCreator());
//        preparedStatement2.setString(10, mediaInfo.getAlbum());
//        preparedStatement2.addBatch();
//
//        return preparedStatement2;
//    }
//
//
//    private JDBC4PreparedStatement insertToSubtitleWordlistTblDTO(Connection conn, List<String> listRubyText)
//            throws SQLException {
//        String compiledQuery = "INSERT OR IGNORE INTO SUBTITLE_WORDLIST('ID', 'SUBTITLE_ID', 'VOCA_TYPE', 'VOCA_ID') "
//                + " VALUES (?, ?, ?, ?)";
//        JDBC4PreparedStatement preparedStatement = (JDBC4PreparedStatement) conn.prepareStatement(compiledQuery);
//        conn.setAutoCommit(false);
//        Integer id = 0;
//        Integer subtitleId = 0;
//
//        for (String rubyText : listRubyText) {
//            Map<Integer, Object> mapSubtitleWordList = getSubtitleWordListDTO(rubyText);
//            List<Integer> vocaIDList = (List<Integer>)mapSubtitleWordList.get(1);
//            List<Integer> vocaTypeList = (List<Integer>)mapSubtitleWordList.get(2);
//            if (vocaIDList.size() == vocaTypeList.size()) {
//                Map<Integer, Integer> mapWordList = new HashMap<Integer, Integer>();
//                for (Integer dispOrder = 0; dispOrder < vocaIDList.size(); dispOrder++) {
//                    if (!(mapWordList.containsKey(vocaIDList.get(dispOrder)))) {
//                        preparedStatement.setInt(1, id++);
//                        preparedStatement.setInt(2, subtitleId);
//                        preparedStatement.setInt(3,  vocaTypeList.get(dispOrder));
//                        preparedStatement.setInt(4, vocaIDList.get(dispOrder));
//
//                        preparedStatement.addBatch();
//                        //같은 자막에서 중복된 단어는 SUBTITLE_WORDLIST의 하나만 넣을려고 함.
//                        mapWordList.put(vocaIDList.get(dispOrder), 0);
//                    }
//                }
//                subtitleId++; //
//            }
//        }
//        return preparedStatement;
//    }

    //이건 나중에 위의거와 합쳐야 한다. For문이 두번 도는거다.
    private LinkedHashMap<Integer, Integer> getWordAppearanceOrder(List<String> listRubyText) {
        Integer id = 1;
        //정렬된맵(Sorted Map)을 쓸려면 LinkedHashMap을 사용한다.
        LinkedHashMap<Integer, Integer> mapWordAppearanceOrder = new LinkedHashMap<>();
        for (String rubyText : listRubyText) {
//        	Map<Integer, Object> mapSubtitleWordList = getSubtitleWordList(rubyText);
            Map<Integer, Object> mapSubtitleWordList = getSubtitleWordListDTO(rubyText);
            List<Integer> vocaIDs = (List<Integer>)mapSubtitleWordList.get(1);
//        	List<String> vocaDisplay = (List<String>)mapSubtitleWordList.get(2);
//        	if (vocaIDs.size() == vocaDisplay.size()) {
            for (Integer dispOrder = 0; dispOrder < vocaIDs.size(); dispOrder++) {
                if (!(mapWordAppearanceOrder.containsKey(vocaIDs.get(dispOrder)))) {
                    mapWordAppearanceOrder.put(vocaIDs.get(dispOrder), id++);
                }
            }
//        	}
        }
        return mapWordAppearanceOrder;
    }


    private Map<Integer, Object> getSubtitleWordListDTO(String rubyText) {
        Map<Integer, Object> mapSubtitleWordList = new HashMap<Integer, Object>();
        List<Integer> vocaIDList = new ArrayList<Integer>();
        List<Integer> vocaTypeList = new ArrayList<Integer>();

        //<span VOCA_TYPE=1 VOCA_ID=10457 VOCA_ORI_ID=10457 VOCA="but" VOCA_DISPLAY="but" PRONOUNCE="bʌt" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="only, just,simply" MEANING_TTS="only, just,simply"><ruby><rb>but</rb><rt>only, just,simply</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=80115 VOCA_ORI_ID=80115 VOCA="she" VOCA_DISPLAY="she" PRONOUNCE="∫i:" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="it,woman" MEANING_TTS="it,woman"><ruby><rb>she</rb><rt>it,woman</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=40875 VOCA_ORI_ID=40995 VOCA="has" VOCA_DISPLAY="has" PRONOUNCE="hæz" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="possess,own,be in possession of" MEANING_TTS="possess,own,be in possession of"><ruby><rb>has</rb><rt>possess,own,be in possession of</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=50608 VOCA_ORI_ID=51400 VOCA="less" VOCA_DISPLAY="less" PRONOUNCE="les" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=3 BOOKMARK=0" MEANING="small,small-scale,compact" MEANING_TTS="small,small-scale,compact"><ruby><rb>less</rb><rt>small,small-scale,compact</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=31429 VOCA_ORI_ID=31429 VOCA="faith" VOCA_DISPLAY="faith" PRONOUNCE="feiθ" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=5 BOOKMARK=0" MEANING="trust,religion" MEANING_TTS="trust,religion"><ruby><rb>faith</rb><rt>trust,religion</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=44930 VOCA_ORI_ID=44930 VOCA="in" VOCA_DISPLAY="in" PRONOUNCE="in" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="present, inside, indoors" MEANING_TTS="present, inside, indoors"><ruby><rb>in</rb><rt>present, inside, indoors</rt></ruby></span>  <br /><span VOCA_TYPE=1 VOCA_ID=100711 VOCA_ORI_ID=100711 VOCA="american" VOCA_DISPLAY="american" PRONOUNCE="əˈmɛrɪkən" KNOW=0 KNOW_PRONOUNCE=0 AMKI_GRADE =1 WORD_LEVEL=999 BOOKMARK=0" MEANING="" MEANING_TTS=""><ruby><rb>American</rb><rt></rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=45674 VOCA_ORI_ID=45667 VOCA="individualism" VOCA_DISPLAY="individualism" PRONOUNCE="ìndəvídʒuəlìzəm" KNOW=1 KNOW_PRONOUNCE=1 AMKI_GRADE =1 WORD_LEVEL=16 BOOKMARK=0" MEANING="single,separate,discrete" MEANING_TTS="single,separate,discrete"><ruby><rb>individualism</rb><rt>single,separate,discrete</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=89411 VOCA_ORI_ID=89411 VOCA="than" VOCA_DISPLAY="than" PRONOUNCE="ðæn" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="how,as" MEANING_TTS="how,as"><ruby><rb>than</rb><rt>how,as</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=43944 VOCA_ORI_ID=43944 VOCA="i" VOCA_DISPLAY="i" PRONOUNCE="ai" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="the imaginary quantity equal to the square root of minus one" MEANING_TTS="the imaginary quantity equal to the square root of minus one"><ruby><rb>I</rb><rt>the imaginary quantity equal to the square root of minus one</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=25593 VOCA_ORI_ID=25593 VOCA="do" VOCA_DISPLAY="do" PRONOUNCE="du:" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="party,ut,bash" MEANING_TTS="party,ut,bash"><ruby><rb>do</rb><rt>party,ut,bash</rt></ruby></span>.
        Document doc = Jsoup.parse(rubyText, "", Parser.xmlParser());

        Elements eleVocaIDs = doc.getElementsByAttribute("VOCA_ID");
        Elements eleVocaDisplays = doc.getAllElements();
        Elements links = doc.select("VOCA_ID"); // a with href
        Elements pngs = doc.select("[VOCA_ID]");

        for (Element e : eleVocaIDs) {
            String vocaID = e.attr("VOCA_ID");
            String vocaType = e.attr("VOCA_TYPE");
            String vocaKnow = e.attr("VOCA_KNOW");
//    		System.out.println("vocaDisplay : '" + vocaDisplay + "'");
            vocaIDList.add(Integer.parseInt(vocaID));
            vocaTypeList.add(Integer.parseInt(vocaType));

        }

        mapSubtitleWordList.put(1, vocaIDList);
        mapSubtitleWordList.put(2, vocaTypeList);

        return mapSubtitleWordList;
    }

    private Map<Integer, Object> getSubtitleWordList(String rubyText) {
        Map<Integer, Object> mapSubtitleWordList = new HashMap<Integer, Object>();
        List<Integer> vocaIDs = new ArrayList<Integer>();
        List<String> vocaDisplays = new ArrayList<String>();

        //<span VOCA_TYPE=1 VOCA_ID=10457 VOCA_ORI_ID=10457 VOCA="but" VOCA_DISPLAY="but" PRONOUNCE="bʌt" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="only, just,simply" MEANING_TTS="only, just,simply"><ruby><rb>but</rb><rt>only, just,simply</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=80115 VOCA_ORI_ID=80115 VOCA="she" VOCA_DISPLAY="she" PRONOUNCE="∫i:" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="it,woman" MEANING_TTS="it,woman"><ruby><rb>she</rb><rt>it,woman</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=40875 VOCA_ORI_ID=40995 VOCA="has" VOCA_DISPLAY="has" PRONOUNCE="hæz" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="possess,own,be in possession of" MEANING_TTS="possess,own,be in possession of"><ruby><rb>has</rb><rt>possess,own,be in possession of</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=50608 VOCA_ORI_ID=51400 VOCA="less" VOCA_DISPLAY="less" PRONOUNCE="les" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=3 BOOKMARK=0" MEANING="small,small-scale,compact" MEANING_TTS="small,small-scale,compact"><ruby><rb>less</rb><rt>small,small-scale,compact</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=31429 VOCA_ORI_ID=31429 VOCA="faith" VOCA_DISPLAY="faith" PRONOUNCE="feiθ" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=5 BOOKMARK=0" MEANING="trust,religion" MEANING_TTS="trust,religion"><ruby><rb>faith</rb><rt>trust,religion</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=44930 VOCA_ORI_ID=44930 VOCA="in" VOCA_DISPLAY="in" PRONOUNCE="in" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="present, inside, indoors" MEANING_TTS="present, inside, indoors"><ruby><rb>in</rb><rt>present, inside, indoors</rt></ruby></span>  <br /><span VOCA_TYPE=1 VOCA_ID=100711 VOCA_ORI_ID=100711 VOCA="american" VOCA_DISPLAY="american" PRONOUNCE="əˈmɛrɪkən" KNOW=0 KNOW_PRONOUNCE=0 AMKI_GRADE =1 WORD_LEVEL=999 BOOKMARK=0" MEANING="" MEANING_TTS=""><ruby><rb>American</rb><rt></rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=45674 VOCA_ORI_ID=45667 VOCA="individualism" VOCA_DISPLAY="individualism" PRONOUNCE="ìndəvídʒuəlìzəm" KNOW=1 KNOW_PRONOUNCE=1 AMKI_GRADE =1 WORD_LEVEL=16 BOOKMARK=0" MEANING="single,separate,discrete" MEANING_TTS="single,separate,discrete"><ruby><rb>individualism</rb><rt>single,separate,discrete</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=89411 VOCA_ORI_ID=89411 VOCA="than" VOCA_DISPLAY="than" PRONOUNCE="ðæn" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="how,as" MEANING_TTS="how,as"><ruby><rb>than</rb><rt>how,as</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=43944 VOCA_ORI_ID=43944 VOCA="i" VOCA_DISPLAY="i" PRONOUNCE="ai" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="the imaginary quantity equal to the square root of minus one" MEANING_TTS="the imaginary quantity equal to the square root of minus one"><ruby><rb>I</rb><rt>the imaginary quantity equal to the square root of minus one</rt></ruby></span> <span VOCA_TYPE=1 VOCA_ID=25593 VOCA_ORI_ID=25593 VOCA="do" VOCA_DISPLAY="do" PRONOUNCE="du:" KNOW=3 KNOW_PRONOUNCE=3 AMKI_GRADE =1 WORD_LEVEL=1 BOOKMARK=0" MEANING="party,ut,bash" MEANING_TTS="party,ut,bash"><ruby><rb>do</rb><rt>party,ut,bash</rt></ruby></span>.
        Document doc = Jsoup.parse(rubyText, "", Parser.xmlParser());

        Elements eleVocaIDs = doc.getElementsByAttribute("VOCA_ID");
        Elements eleVocaDisplays = doc.getAllElements();
        Elements links = doc.select("VOCA_ID"); // a with href
        Elements pngs = doc.select("[VOCA_ID]");

        for (Element e : eleVocaIDs) {
            String strVocaID = e.attr("VOCA_ID");
//    		System.out.println("strVocaID : '" + strVocaID + "'");
//    		vocaDisplays.add(strVocaID);
            vocaIDs.add(Integer.parseInt(strVocaID));
        }

        for (Element e : eleVocaIDs) {
            String vocaDisplay = e.attr("VOCA_DISPLAY");
//    		System.out.println("vocaDisplay : '" + vocaDisplay + "'");
            vocaDisplays.add(vocaDisplay);
        }
        mapSubtitleWordList.put(1, vocaIDs);
        mapSubtitleWordList.put(2, vocaDisplays);
        return mapSubtitleWordList;
    }
//    protected void insertWordToSqlite(Map<String, JSONObject> mapUniqueWords, String folderNew, String sqlite) {
////		ResultSet rs = null;
//        try {
//            String driver = "org.sqlite.JDBC";
//            Class.forName(driver);
//            String dbName = folderNew + File.separator + sqlite;
//            String dbUrl = "jdbc:sqlite:" + dbName;
//            Connection conn;
//
//            conn = DriverManager.getConnection(dbUrl);
////			PreparedStatement preparedStatement;
//            JDBC4PreparedStatement preparedStatement;
//            String compiledQuery = "INSERT OR IGNORE INTO DIC('WORDID', 'WORDWITHPOS', 'BOOKMARK', 'VOCA_KNOW', 'VOCA_KNOWPRONOUNCE', 'MEANING', "
//                    + "'POSOFWORD', 'POSOFWORDBASEFROM', 'PRONOUNCE', 'PRONOUNCE_ANALYZER', 'PRONOUNCEWORDORI', 'WORD', "
//                    + "'WORDLEVEL', 'WORDORI', 'WORDLIST_KEY', 'WORD_WITHCONJUGATION', 'FREQUENCY', 'SERIAL_ID', 'EXISTIN_SERVERDIC', 'WORDORI_ID') "
//                    + " VALUES" + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//            preparedStatement = (JDBC4PreparedStatement) conn.prepareStatement(compiledQuery);
//            conn.setAutoCommit(false);
//            for (String strWordWithPos : mapUniqueWords.keySet()) {
//                JSONObject jsonWord = mapUniqueWords.get(strWordWithPos);
//                Integer wordID = (Integer) jsonWord.get(Constants.KEY_WORDID);
//                Integer wordOriID = (Integer) jsonWord.get(Constants.KEY_WORDORI_ID);
//                boolean blnBookmarked = (boolean) jsonWord.get(Constants.KEY_BOOKMARK);
//                Integer bookmark = (blnBookmarked) ? 1 : 0;
//                Integer vocaKnow = (Integer) jsonWord.get(Constants.KEY_VOCA_KNOW);
//                Integer vocaKnowPronounce = (Integer) jsonWord.get(Constants.KEY_VOCA_KNOWPRONOUNCE);
//                String Meaning = jsonWord.getString(Constants.KEY_MEANING);
//                String posOfWord = (String) jsonWord.get(Constants.KEY_POS_WORD);
//                String posOfWordbaseform = (String) jsonWord.get(Constants.KEY_POS_WORD_BASEFORM);
//                String Pronounce = jsonWord.getString(Constants.KEY_PRONOUNCE);
//                String PronounceFromAnalyzer = (String) jsonWord.get(Constants.KEY_PRONOUNCE_From_Analyzer);
//                String PronounceWordOri = (String) jsonWord.get(Constants.KEY_PRONOUNCE_WORDORI);
//                String Word = jsonWord.getString(Constants.KEY_WORD);// word가 "null"이란 글자일때 (String)jsonWord.get(Constants.KEY_WORD);를 하면 예외가 난다.
//                Integer WordLevel = (Integer) jsonWord.get(Constants.KEY_WORDLEVEL);
//                String WordOri = jsonWord.getString(Constants.KEY_WORDORI);
//                String WordListKey = jsonWord.getString(Constants.KEY_WORDLIST_KEY);
//                String WordWithConjugation = (String) jsonWord.get(Constants.KEY_WORD_WithConjugation);
//                Integer frequency = (Integer) jsonWord.get(Constants.KEY_FREQUENCY);
//                Integer serialID = (Integer) jsonWord.get(Constants.KEY_SERIAL_ID_FOR_WORDS);
//                String existInServerDic = (String) jsonWord.get(Constants.KEY_EXISTIN_SERVERDIC);
//
//                preparedStatement.setInt(1, wordID);
//                preparedStatement.setString(2, strWordWithPos + "_" + posOfWord);
//                preparedStatement.setInt(3, bookmark);
//                preparedStatement.setInt(4, vocaKnow);
//                preparedStatement.setInt(5,  vocaKnowPronounce);
//                preparedStatement.setString(6,  Meaning);
//                preparedStatement.setString(7,  (String) jsonWord.get(Constants.KEY_POS_WORD));
//                preparedStatement.setString(8,  (String) jsonWord.get(Constants.KEY_POS_WORD_BASEFORM));
//                preparedStatement.setString(9,  Pronounce);
//                preparedStatement.setString(10,  (String) jsonWord.get(Constants.KEY_PRONOUNCE_From_Analyzer));
//                preparedStatement.setString(11,  (String) jsonWord.get(Constants.KEY_PRONOUNCE_WORDORI));
//                preparedStatement.setString(12,  Word);
//                preparedStatement.setInt(13,  (Integer) jsonWord.get(Constants.KEY_WORDLEVEL));
//                preparedStatement.setString(14,  WordOri);
//                preparedStatement.setString(15,  WordListKey);
//                preparedStatement.setString(16,  (String) jsonWord.get(Constants.KEY_WORD_WithConjugation));
//                preparedStatement.setInt(17,  (Integer) jsonWord.get(Constants.KEY_FREQUENCY));
//                preparedStatement.setInt(18,  (Integer) jsonWord.get(Constants.KEY_SERIAL_ID_FOR_WORDS));
//                preparedStatement.setString(19,  (String) jsonWord.get(Constants.KEY_EXISTIN_SERVERDIC));
//                preparedStatement.setInt(20,  wordOriID);
//                preparedStatement.addBatch();
//            }
//            int[] inserted = preparedStatement.executeBatch();
//            conn.setAutoCommit(true);
//
//            //create table
////	        Statement st = conn.createStatement();
////	        st.executeUpdate("CREATE table village (id int, name varchar(20))");
//            //지우지말것
////-------------------------------------------------------------------------------------
////	        st.executeUpdate("INSERT INTO village VALUES (111, 'Concretepage')");
////	        //select
//            String query = "SELECT id, name from village";
//            query = "";
////	       rs = st.executeQuery(query);
////	       while(rs.next()) {
////	          int id = rs.getInt(1);
////	          String name = rs.getString(2);
////	          logger.info("id:"+ id+ ", name: "+ name);
////	       }
////	       //	delete
////	       st.executeUpdate("DELETE from village");
////-------------------------------------------------------------------------------------
//            //st.close();
//            preparedStatement.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } finally {
////    	    try {
////				rs.close();
////			} catch (SQLException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//        }
//
//    }
//
//    protected void insertWordToSqliteDTO(Map<String, DTO_VOCA_DETAIL_RUBY_TEXT> mapUniqueWords, String folderNew, String sqlite) {
////		ResultSet rs = null;
//        try {
//            String driver = "org.sqlite.JDBC";
//            Class.forName(driver);
//            String dbName = folderNew + File.separator + sqlite;
//            String dbUrl = "jdbc:sqlite:" + dbName;
//            Connection conn;
//
//            conn = DriverManager.getConnection(dbUrl);
////			PreparedStatement preparedStatement;
//            JDBC4PreparedStatement preparedStatement;
//            String compiledQuery = "INSERT OR IGNORE INTO DIC('WORDID', 'WORDWITHPOS', 'BOOKMARK', 'VOCA_KNOW', 'VOCA_KNOWPRONOUNCE', 'MEANING', "
//                    + "'POSOFWORD', 'POSOFWORDBASEFROM', 'PRONOUNCE', 'PRONOUNCE_ANALYZER', 'PRONOUNCEWORDORI', 'WORD', "
//                    + "'WORDLEVEL', 'WORDORI', 'WORDLIST_KEY', 'WORD_WITHCONJUGATION', 'FREQUENCY', 'SERIAL_ID', 'EXISTIN_SERVERDIC', 'WORDORI_ID') "
//                    + " VALUES" + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//            preparedStatement = (JDBC4PreparedStatement) conn.prepareStatement(compiledQuery);
//            conn.setAutoCommit(false);
//            for (String strWordWithPos : mapUniqueWords.keySet()) {
//                DTO_VOCA_DETAIL_RUBY_TEXT dtoVoca= mapUniqueWords.get(strWordWithPos);
//                preparedStatement.setInt(1, dtoVoca.getVOCA_ID());
//                preparedStatement.setString(2, strWordWithPos + "_" + dtoVoca.getPOS_VOCA());
//                preparedStatement.setInt(3, dtoVoca.getBOOKMARK());
//                preparedStatement.setInt(4, dtoVoca.getVOCA_KNOW());
//                preparedStatement.setInt(5,  dtoVoca.getVOCA_KNOWPRONOUNCE());
//                preparedStatement.setString(6, dtoVoca.getMEANING());
//                preparedStatement.setString(7, dtoVoca.getPOS_VOCA());
//                preparedStatement.setString(8, dtoVoca.getPOS_VOCA_BASEFORM());
//                preparedStatement.setString(9, dtoVoca.getPRONOUNCE());
//                preparedStatement.setString(10, dtoVoca.getPRONOUNCE_From_Analyzer());
//                preparedStatement.setString(11, dtoVoca.getPRONOUNCE_VOCAORI());
//                preparedStatement.setString(12, dtoVoca.getVOCA());
//                preparedStatement.setInt(13, dtoVoca.getVOCA_LEVEL());
//                preparedStatement.setString(14, dtoVoca.getVOCAORI());
//                preparedStatement.setString(15, dtoVoca.getVOCALIST_KEY());
//                preparedStatement.setString(16, dtoVoca.getVOCA_WithConjugation());
//                preparedStatement.setInt(17, dtoVoca.getFREQUENCY());
//                preparedStatement.setInt(18, dtoVoca.getSERIAL_ID_FOR_VOCAS());
//                preparedStatement.setString(19, dtoVoca.getEXISTIN_SERVERDIC());
//                preparedStatement.setInt(20, dtoVoca.getVOCAORI_ID());
//                preparedStatement.addBatch();
//            }
//            int[] inserted = preparedStatement.executeBatch();
//            conn.setAutoCommit(true);
//
//            preparedStatement.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

//    protected String makeFolderNameWithDate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        Date date = new Date();
//        logger.info(dateFormat.format(date)); //2016/11/16 12:08:43
//        return dateFormat.format(date);
//    }

//    protected void copyJS_CSSToFolder(String destFolder) {
////		try{
//        File FILE_srcJSFolder = new File(srcJSFolder);
//        File FILE_srcStyleFolder = new File(srcStylesFolder);
////       	File FILE_srcsqliteFilePath = new File(srcSqliteFilePath);
//
//        File destJSFolder = new File(destFolder + File.separator + FILE_srcJSFolder.getName());
//        File destStyleFolder = new File(destFolder + File.separator + FILE_srcStyleFolder.getName());
////    	File destsqliteFilePath = new File(destFolder + File.separator + FILE_srcsqliteFilePath.getName());
////        logger.info("destJSFolder.getName() : " + destJSFolder.getName());
////        logger.info("destStyleFolder : " + destStyleFolder.getName());
//
//
//        copyFolder(FILE_srcJSFolder,destJSFolder);
//        copyFolder(FILE_srcStyleFolder,destStyleFolder);
//        copySqliteToFolder(destFolder);
////    		copyFolder(FILE_srcsqliteFilePath,destsqliteFilePath);
////    	}catch(IOException e){
////    		e.printStackTrace();
////    	}
//    }

//    protected void copySqliteToFolder(String destFolder) {
////		try{
//        File FILE_srcsqliteFilePath = new File(srcSqliteFilePath);
//
//        File destsqliteFilePath = new File(destFolder + File.separator + FILE_srcsqliteFilePath.getName());
//        copyFolder(FILE_srcsqliteFilePath,destsqliteFilePath);
////    	}catch(IOException e){
////    		e.printStackTrace();
////    	}
//    }
//
//    protected void copyFolder(File src, File dest) {
//        try {
//            if(src.isDirectory()){
//                Files.createDirectories(Paths.get(dest.getAbsolutePath()));
//                //list all the directory contents
//                String files[] = src.list();
//
//                //construct the src and dest file structure
//                for (String file : files) {
//                    if (file.equals(Constants.FILE_DSStore)) {
//                        continue;
//                    }
//                    File srcFile = new File(src, file);
//                    File destFile = new File(dest, file);
//                    //recursive copy
//                    copyFolder(srcFile,destFile);
//                }
//
//            }else{
//                //if file, then copy it
//                //Use bytes stream to support all file types
//                InputStream in = new FileInputStream(src);
//                OutputStream out = new FileOutputStream(dest);
//
//                byte[] buffer = new byte[1024];
//
//                int length;
//                //copy the file content in bytes
//                while ((length = in.read(buffer)) > 0){
//                    out.write(buffer, 0, length);
//                }
//
//                in.close();
//                out.close();
////                logger.info("File copied from " + src + " to " + dest);
//            }
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    private void deleteDir(File file) {
//        File[] contents = file.listFiles();
//        if (contents != null) {
//            for (File f : contents) {
//                deleteDir(f);
//            }
//        }
//        file.delete();
//    }
//
//
//    private void NotdeleteDir(File file) {
//        File[] contents = file.listFiles();
//        if (contents != null) {
//        }
////        logger.debug(contents);
//    }
//
//
//
//    //현재 HTML에 JS와 CSS를 삽입하기 위한 상대경로를 가져온다.
//    protected String getRelatviePahtOfJSCSS(String strHTMLFilePath, String strFolderName, String strFileName, String folderNew) {
//
//        String strFileNameWithFolder = File.separator + strFolderName + File.separator + strFileName;
//        String strJSCSSFilePath = folderNew + strFileNameWithFolder;
//
//
//        Path htmlFile = Paths.get(new File(strHTMLFilePath).getParent());
//        Path jscssFile = Paths.get(strJSCSSFilePath);
//        Path relativePath = htmlFile.relativize(jscssFile);
//
//        return relativePath.toString();
//    }




//
//    protected String repleaceUidStuyLang(String cssString, HttpServletRequest req) {
//        String UID = getUID(req);
//        String studyLang = getStudyLang(req);
//        String dispMeaningLang = getDisyMeaningLang(req);
//        cssString = cssString.replaceFirst("var uid = \"\";", "var uid = \"" +  UID + "\";");
//        //TODO 유저타입은 나중에 DB로부터 가져올
//        cssString = cssString.replaceFirst("var userType = \"\";", "var userType = \"" +  Constants.USER_TYPE_ADMIN + "\";");
//        cssString = cssString.replaceFirst("var studyLang = \"\";", "var studyLang = \"" + studyLang + "\";");
//        cssString = cssString.replaceFirst("var dispMeaningLang = \"\";", "var dispMeaningLang = \"" + dispMeaningLang + "\";");
//        if (studyLang.equals(Constants.LANG_JP) || studyLang.equals(Constants.LANG_CH_S)) {
//            cssString = cssString.replaceFirst("var blnShowMeaningAsRubyText = true;", "var blnShowMeaningAsRubyText = false;");
//        }
//
//
//
//        return cssString;
//    }
//
//    protected void writeTextIntoContentHTMLDTO(String strFilePath, String strToReplace, String strContents)
//    {
//        try {
////			String newContents = strContents.replace("%@", strToReplace);
//            String strToReplaceWithHTMLTAG = strToReplace.replaceAll("&", "&#38;");
//            strToReplaceWithHTMLTAG = strToReplace.replaceAll(Constants.SPACE, Constants.HTMLTAG_SPACE_nbsp);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\r\\n", Constants.SPACE + Constants.HTMLTAG_BR);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\r", Constants.SPACE + Constants.HTMLTAG_BR);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\n", Constants.SPACE + Constants.HTMLTAG_BR);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\t", Constants.HTMLTAG_TAB_9);
//
//            String newContents = strContents.replace("%@", strToReplaceWithHTMLTAG);
//
//            Files.write(Paths.get(strFilePath), newContents.getBytes());
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    protected void writeTextIntoContentHTML(String strFilePath, String strToReplace, String strContents, HttpServletRequest req)
//    {
//        try {
////			String newContents = strContents.replace("%@", strToReplace);
//            String strToReplaceWithHTMLTAG = strToReplace.replaceAll("&", "&#38;");
//            strToReplaceWithHTMLTAG = strToReplace.replaceAll(Constants.SPACE, Constants.HTMLTAG_SPACE_nbsp);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\r\\n", Constants.SPACE + Constants.HTMLTAG_BR);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\r", Constants.SPACE + Constants.HTMLTAG_BR);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\n", Constants.SPACE + Constants.HTMLTAG_BR);
//            strToReplaceWithHTMLTAG = strToReplaceWithHTMLTAG.replaceAll("\\t", Constants.HTMLTAG_TAB_9);
//
//            String newContents = strContents.replace("%@", strToReplaceWithHTMLTAG);
//
//            Files.write(Paths.get(strFilePath), newContents.getBytes());
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//
//    public boolean zip(String src)
//    {
//        try
//        {
//            ZipFile zipFile = new ZipFile(src + Constants.FILE_DOT + Constants.FILEEXT_zip);
//
//            ZipParameters parameters = new ZipParameters();
//            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
//            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
//
//            File[] contents = new File(src).listFiles();
//
//            // remove it from the contents :
//            ArrayList<File> contentsList = new ArrayList<>(Arrays.asList(contents));
//
//            for(File f : contentsList)  //contents)
//            {
//                if(f.isDirectory())
//                {
//                    zipFile.addFolder(f, parameters);
//                }
//                else
//                {
//                    zipFile.addFile(f, parameters);
//                }
//            }
//
//            return true;
//        }
//        catch (ZipException e)
//        {
//            e.printStackTrace();
//            return false;
//        }
//    }
//


    protected String getFileContents(Path path) {
        String strContents = "";
        try {
            strContents = new String(Files.readAllBytes(path));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return strContents;
    }
    // \r, \r\n을 \n으로 변경한다.
    public static String convertNewlineToSlashN(String str)
    {
        try
        {
            return str.replaceAll("\r|\r\n", "\n");
        }
        catch(Exception nfe)
        {

        }
        return "";
    }
    protected String getSimpleTextWithoutHTMLTag(String html)
    {
        html = convertNewlineToSlashN(html);
        Document doc= Jsoup.parse(html);
        //HTML을 정렬하지 않는다.
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
        //<br> <p>등을 나중에 지울때 new line은 남게 여기서 추가해준다.
        //select all <br> tags and append \n after that
        doc.select("br").after("\n");
        //select all <p> tags and prepend \n before that
        doc.select("p").before("\n");

        //new line을 보존할려고 하는거다...
        //ref : https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
        String text = Jsoup.clean(doc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));

        text = text.replaceAll("\n+", "\n");
        //지우지말것, 이건 new line이 보존안된 상태로 html tag없이 text로만 변환시켜준다.
//    	String text2 = Jsoup.parse(html).text();
        return text;
    }

    protected boolean renameFile(String old_name, String new_name)
    {
        File old_file = new File(old_name), new_file = new File(new_name);
        return old_file.renameTo(new_file) ? true : false;
    }

//    protected Map<String, Object> addDalReadHTMLTagToString(Map<String, List<String>> mapAllTextWithoutHTMLTag) {
//        Map<String, Object> returnVal = null;
//        try {
//            String studyLang = Constants.LANG_EN;
//            String dispMeaningLang = Constants.LANG_KO;
//            returnVal = dalRaedTextService.getEpubResult(mapAllTextWithoutHTMLTag, studyLang, dispMeaningLang);
//        } catch (Exception e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        return returnVal;
//    }

    protected String getFileNameToBeDTO(DTO_INPUT_MAKE_RUBY dto){
        return dto.getEPUB_FILE_NAME_TO_BE();
    }

    protected String getStudyLang(){
        return Constants.LANG_EN;
    }

    protected Integer getLangCodeFromLangName(String langName) {
        Integer langCode = Constants.LANGCODE_EN;
        try {
            switch (langName.toUpperCase()) {
                case Constants.LANG_CH_S:
                    langCode = Constants.LANGCODE_CH_S;
                    break;
                case Constants.LANG_JP:
                    langCode = Constants.LANGCODE_JP;
                    break;
                case Constants.LANG_KO:
                    langCode = Constants.LANGCODE_KO;
                    break;
                case Constants.LANG_EN:
                    langCode = Constants.LANGCODE_EN;
                    break;
                case Constants.LANG_HANJA:
                    langCode = Constants.LANGCODE_HANJA;
                    break;
                case Constants.LANG_VI:
                    langCode = Constants.LANGCODE_CH_S;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return langCode;
    }

    // 영어일때 알파벳이면 true를 리턴 (영어가 아니면 전부 true를 리턴)
    public boolean isAlphagetInEnglish(String strOneChar) {
        if (strOneChar.matches("[\\u0041-\\u005A]")
                || strOneChar.matches("[\\u0061-\\u007A]")) {
//			Uppercase Latin Alphabet	U+0041 to U+005A
//			Lowercase Latin Alphabet	U+0061 to U+007A
            return true;
        }
        return false;
    }
}