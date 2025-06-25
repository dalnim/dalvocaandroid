package com.dalread.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VideoInformationModel implements Parcelable {

    private int id;
    private List<Genre> genres;
    private String name;
    private String title;
    private String overview;
    private String overviewMotherTongue;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("still_path")
    private String stillPath;
    private Credits credits;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("first_air_date")
    private String firstAirDate;
    @SerializedName("air_date")
    private String airDate;
    @SerializedName("media_type")
    private String mediaType;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("original_name")
    private String originalName;
    @SerializedName("known_for")
    private List<VideoInformationModel> knownFor;
    private List<VideoInformationModel> seasons;
    private List<VideoInformationModel> episodes;
    @SerializedName("episode_count")
    private int episodeCount;
    @SerializedName("season_number")
    private int seasonNumber;
    @SerializedName("episode_number")
    private int episodeNumber;
    @SerializedName("videos")
    private Trailers trailers;
    @SerializedName("alternative_titles")
    private AlternativeTitles alternativeTitles;

    // parser information from Credits;
    private List<People> director;
    private List<People> writer;
    private String genre;
    private String path;
    private String date;
    private String nameDisplay;
    private String originalNameDisplay;

    public VideoInformationModel() {
        this(0, new ArrayList<>(), Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK,Constant.BASE_BLANK,
                Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK,
                null, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK,
                Constant.BASE_BLANK,
                new ArrayList<>(), new ArrayList<>(), Constant.BASE_BLANK,
                Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK,
                Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK,
                0);
        credits = new Credits();
        knownFor = new ArrayList<>();
        seasons = new ArrayList<>();
        episodes = new ArrayList<>();
        trailers = new Trailers();
        alternativeTitles = new AlternativeTitles();
    }

    public VideoInformationModel(int id, List<Genre> genres, String name, String title, String overview, String overviewMotherTongue,
                                 String posterPath, String backdropPath, String stillPath,
                                 Credits credits, String releaseDate, String firstAirDate, String airDate,
                                 String mediaType,
                                 List<People> director, List<People> writer, String genre,
                                 String path, String date, String display, String originalNameDisplay,
                                 String originalName, String originalTitle,
                                 int episodeCount) {
        this.id = id;
        this.genres = genres;
        this.name = name;
        this.title = title;
        this.overview = overview;
        this.overviewMotherTongue = overviewMotherTongue;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.stillPath = stillPath;
        this.credits = credits;
        this.releaseDate = releaseDate;
        this.firstAirDate = firstAirDate;
        this.airDate = airDate;
        this.mediaType = mediaType;
        this.director = director;
        this.writer = writer;
        this.genre = genre;
        this.path = path;
        this.date = date;
        this.nameDisplay = display;
        this.originalNameDisplay = originalNameDisplay;
        this.originalName = originalName;
        this.originalTitle = originalTitle;
        this.episodeCount = episodeCount;
    }

    protected VideoInformationModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        title = in.readString();
        overview = in.readString();
        overviewMotherTongue = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        stillPath = in.readString();
        credits = in.readParcelable(Credits.class.getClassLoader());
        releaseDate = in.readString();
        firstAirDate = in.readString();
        airDate = in.readString();
        mediaType = in.readString();
        originalTitle = in.readString();
        originalName = in.readString();
        knownFor = in.createTypedArrayList(VideoInformationModel.CREATOR);
        seasons = in.createTypedArrayList(VideoInformationModel.CREATOR);
        episodes = in.createTypedArrayList(VideoInformationModel.CREATOR);
        director = in.createTypedArrayList(People.CREATOR);
        writer = in.createTypedArrayList(People.CREATOR);
        genre = in.readString();
        path = in.readString();
        date = in.readString();
        nameDisplay = in.readString();
        originalNameDisplay = in.readString();
        episodeCount = in.readInt();
        seasonNumber = in.readInt();
        episodeNumber = in.readInt();
        trailers = in.readParcelable(Trailers.class.getClassLoader());
        alternativeTitles = in.readParcelable(AlternativeTitles.class.getClassLoader());
    }

    public static final Creator<VideoInformationModel> CREATOR = new Creator<VideoInformationModel>() {
        @Override
        public VideoInformationModel createFromParcel(Parcel in) {
            return new VideoInformationModel(in);
        }

        @Override
        public VideoInformationModel[] newArray(int size) {
            return new VideoInformationModel[size];
        }
    };

    public int getId() {
        if (isHasKnownFor()) {
            return getKnownForFirstItem().getId();
        }
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

//    public void appendOverview(String overview) {
//        this.overview = this.overview + "\n\n" + overview;
//    }


    public String getOverviewMotherTongue() {
        return overviewMotherTongue;
    }

    public void setOverviewMotherTongue(String overviewMotherTongue) {
        this.overviewMotherTongue = overviewMotherTongue;
    }

    public String getPosterPath() {
        return Utils.isEmpty(posterPath) ? getBackdropPath() : posterPath;
    }

    public String getPosterURL() {
        return getImagePath_SmallSize(getPath());
    }

    public String getOriginalSizePosterURL() {
        return getImagePath_OriginalSize(getPath());
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getBackdropURL() {
        return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_MEDIUM_SIZE + getBackdropPath();
    }

    public String getBackdropURLOriginalSize() {
        return getImagePath_OriginalSize(getBackdropPath());
    }


    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public String getStillPathURL() {
        return getImagePath_SmallSize(getStillPath());
    }

    public String getImagePath_SmallSize(String name) {
        return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_SMALL_SIZE + name;
    }

    public String getImagePath_MediumSize(String name) {
        return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_MEDIUM_SIZE + name;
    }

    public String getImagePath_BigSize(String name) {
        return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_BIG_SIZE + name;
    }

    public String getImagePath_OriginalSize(String name) {
        return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_ORIGINAL_SIZE + name;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public String getMediaType() {
//        if (isHasKnownFor()) {
//            return getKnownForFirstItem().getMediaType();
//        }
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isMediaTypeTV() {
        return mediaType.equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_TV);
    }

    public boolean isMediaTypeMovie() {
        return mediaType.equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_MOVIE);
    }

    public boolean isMediaTypePerson() {
        return mediaType.equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_PERSON);
    }

    public List<People> getDirector() {
        return director;
    }

    public void setDirector(List<People> director) {
        this.director = director;
    }

    public List<People> getWriter() {
        return writer;
    }

    public void setWriter(List<People> writer) {
        this.writer = writer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNameDisplay() {
        return nameDisplay;
    }

    public void setNameDisplay(String nameDisplay) {
        this.nameDisplay = nameDisplay;
    }

    public String getOriginalNameDisplay() {
        return originalNameDisplay;
    }

    public void setOriginalNameDisplay(String originalNameDisplay) {
        this.originalNameDisplay = originalNameDisplay;
    }

    public String getNameDisplayWithOrigianlName() {
        if (Utils.isEmpty(originalNameDisplay) ||
            originalNameDisplay.equals(nameDisplay)) {
            return nameDisplay;
        } else {
            return nameDisplay + " [" + originalNameDisplay + "]";
        }
    }

    public List<VideoInformationModel> getKnownFor() {
        return knownFor;
    }

    public void setKnownFor(List<VideoInformationModel> knownFor) {
        this.knownFor = knownFor;
    }

    public boolean isHasKnownFor() {
        return knownFor != null && !knownFor.isEmpty();
    }

    public VideoInformationModel getKnownForFirstItem() {
        return knownFor.get(0);
    }

    public List<VideoInformationModel> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<VideoInformationModel> seasons) {
        this.seasons = seasons;
    }

    public boolean isHasSeasons() {
        return getSeasons() != null && !getSeasons().isEmpty();
    }

    public List<VideoInformationModel> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<VideoInformationModel> episodes) {
        this.episodes = episodes;
    }

    public boolean isHasEpisodes() {
        return getEpisodes() != null && !getEpisodes().isEmpty();
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Trailers getTrailers() {
        return trailers;
    }

    public void setTrailers(Trailers trailers) {
        this.trailers = trailers;
    }

    public String getTrailer() {
        String content = Constant.BASE_BLANK;
        if (getTrailers() != null && getTrailers().trailers != null && !getTrailers().trailers.isEmpty()) {
            for (Trailer v : getTrailers().trailers) {
                if (v.getType().equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_TRAILER)) {
                    if (v.getSite().equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_YOUTUBE)) {
                        content = v.getKey();
                        break;
                    }
                }
            }
            if (Utils.isEmpty(content)) {
                content = getTrailers().trailers.get(0).getKey();
            }
            if (!Utils.isEmpty(content)) {
                content = Constant.URL_YOUTUBE + content;
            }
        }
        return content;
    }

    public void generateData() {
        if (genres != null && !genres.isEmpty()) {
            for (Genre g : genres) {
                genre = StringUtils.addString(genre, g.getName());
            }
        }

        if (credits != null) {
            if (credits.crew != null && !credits.crew.isEmpty()) {
                for (People c : credits.crew) {
                    if (c.getJob().equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_DIRECTOR)) {
                        director.add(c);
                    } else if (c.getJob().equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_WRITER)) {
                        writer.add(c);
                    }
                }
            }
        }
        generateDateVideo();
    }

    public void generateDateVideo() {
        if (mediaType == null) {
            mediaType = Constant.BASE_BLANK;
        }
        if (mediaType.equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_PERSON)) {
            if (isHasKnownFor()) {
                this.nameDisplay = getKnownForFirstItem().getTitle();
                this.originalNameDisplay = getKnownForFirstItem().getOriginalTitle();
                this.path = getKnownForFirstItem().getPosterPath();
                this.date = getKnownForFirstItem().getReleaseDate();

            } else {
                this.nameDisplay = getName();
                this.originalNameDisplay = getOriginalName();
            }
        } else if (mediaType.equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_TV)) {
            if (isHasSeasons()) {
                for (VideoInformationModel item : seasons) {
                    item.setMediaType(mediaType);
                    item.setNameDisplay(item.getName() + "\n" + item.getEpisodeCount());
                    item.setOriginalNameDisplay((item.getOriginalName() + "\n" + item.getEpisodeCount()));
                    item.setPath(item.getPosterPath());
                    item.setDate(item.getAirDate());
                }
            } if (isHasEpisodes()) {
                for (VideoInformationModel item : episodes) {
                    item.setMediaType(mediaType);
                    item.setNameDisplay(item.getName());
                    item.setOriginalNameDisplay(item.getOriginalName());
                    item.setPath(item.getStillPath());
                    item.setDate(item.getAirDate());
                }
            }
            this.nameDisplay = getName();
            this.originalNameDisplay = getOriginalName();
            this.path = getPosterPath();
            this.date = getFirstAirDate();
        } else {
            // default KEY MOVIE
            this.nameDisplay = getTitle();
            this.originalNameDisplay = getOriginalTitle();
            this.path = getPosterPath();
            this.date = getReleaseDate();
        }
    }

    public AlternativeTitles getAlternativeTitles() {
        return alternativeTitles;
    }

    public void setAlternativeTitles(AlternativeTitles alternativeTitles) {
        this.alternativeTitles = alternativeTitles;
    }

    public String getAlternativeTitle(String languageIso) {
        if (getAlternativeTitles() == null)
            return Constant.BASE_BLANK;
        return getAlternativeTitles().getTitle(languageIso);
    }

    public String generateFolderName() {
        return getMediaType() + "_" + getId();
    }

    @Override
    public String toString() {
        return "VideoInformationModel{" +
                "id=" + id +
                ", genres=" + genres +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", overviewMotherTongue='" + overviewMotherTongue + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", credits=" + credits +
                ", releaseDate='" + releaseDate + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", director='" + director + '\'' +
                ", writer='" + writer + '\'' +
                ", genre='" + genre + '\'' +
                ", knownFor=" + knownFor +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(overviewMotherTongue);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(stillPath);
        parcel.writeParcelable(credits, i);
        parcel.writeString(releaseDate);
        parcel.writeString(firstAirDate);
        parcel.writeString(airDate);
        parcel.writeString(mediaType);
        parcel.writeString(originalTitle);
        parcel.writeString(originalName);
        parcel.writeTypedList(knownFor);
        parcel.writeTypedList(seasons);
        parcel.writeTypedList(episodes);
        parcel.writeTypedList(director);
        parcel.writeTypedList(writer);
        parcel.writeString(genre);
        parcel.writeString(path);
        parcel.writeString(date);
        parcel.writeString(nameDisplay);
        parcel.writeString(originalNameDisplay);
        parcel.writeInt(episodeCount);
        parcel.writeInt(seasonNumber);
        parcel.writeInt(episodeNumber);
        parcel.writeParcelable(trailers, i);
        parcel.writeParcelable(alternativeTitles, i);
    }

    public static class Genre implements Parcelable {
        public String id;
        private String name;

        public Genre(String id, String name) {
            this.id = id;
            this.name = name;
        }

        protected Genre(Parcel in) {
            id = in.readString();
            name = in.readString();
        }

        public static final Creator<Genre> CREATOR = new Creator<Genre>() {
            @Override
            public Genre createFromParcel(Parcel in) {
                return new Genre(in);
            }

            @Override
            public Genre[] newArray(int size) {
                return new Genre[size];
            }
        };

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Genre{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(name);
        }
    }

    public static class Credits implements Parcelable {
        private List<People> cast;
        private List<People> crew;
        @SerializedName("guest_stars")
        private List<People> guestStars;

        public Credits() {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        public Credits(List<People> cast, List<People> crew, List<People> guestStars) {
            this.cast = cast;
            this.crew = crew;
            this.guestStars = guestStars;
        }

        protected Credits(Parcel in) {
            cast = in.createTypedArrayList(People.CREATOR);
            crew = in.createTypedArrayList(People.CREATOR);
            guestStars = in.createTypedArrayList(People.CREATOR);
        }

        public static final Creator<Credits> CREATOR = new Creator<Credits>() {
            @Override
            public Credits createFromParcel(Parcel in) {
                return new Credits(in);
            }

            @Override
            public Credits[] newArray(int size) {
                return new Credits[size];
            }
        };

        public List<People> getCast() {
            return cast;
        }

        public void setCast(List<People> cast) {
            this.cast = cast;
        }

        public List<People> getCrew() {
            return crew;
        }

        public void setCrew(List<People> crew) {
            this.crew = crew;
        }

        public List<People> getGuestStars() {
            return guestStars;
        }

        public void setGuestStars(List<People> guestStars) {
            this.guestStars = guestStars;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeTypedList(cast);
            parcel.writeTypedList(crew);
            parcel.writeTypedList(guestStars);
        }
    }

    public static class People implements Parcelable {
        private int id;
        @SerializedName("original_name")
        private String name;
        private String job;
        private String character;
        @SerializedName("profile_path")
        private String path;

        public People() {
            this(0, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK);
        }

        public People(String job) {
            this(0, Constant.BASE_BLANK, job, Constant.BASE_BLANK, Constant.BASE_BLANK);
        }

        public People(int id, String name, String job, String character, String path) {
            this.id = id;
            this.name = name;
            this.job = job;
            this.character = character;
            this.path = path;
        }

        protected People(Parcel in) {
            id = in.readInt();
            name = in.readString();
            job = in.readString();
            character = in.readString();
            path = in.readString();
        }

        public static final Creator<People> CREATOR = new Creator<People>() {
            @Override
            public People createFromParcel(Parcel in) {
                return new People(in);
            }

            @Override
            public People[] newArray(int size) {
                return new People[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getCharacter() {
            return character;
        }

        public void setCharacter(String character) {
            this.character = character;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getImagePath() {
            return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_PEOPLE_URL + path;
        }

        public String getOriginalSizeImagePath() {
            return Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_PEOPLE_URL_ORIGINAL + path;
        }

        public String getPersonURL() {
            return StringUtils.createURL(Constant.PLAYER.THE_MOVIE_DB.BASE_PERSON_URL + id + getName(), "-");
        }

        @Override
        public String toString() {
            return "People{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", job='" + job + '\'' +
                    ", character='" + character + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(name);
            parcel.writeString(job);
            parcel.writeString(character);
            parcel.writeString(path);
        }
    }

    public static VideoInformationModel parserVideoInformation(String content, String mediaType) {
        Gson gson = new GsonBuilder().create();
        VideoInformationModel video = gson.fromJson(content, VideoInformationModel.class);
        if (video == null) {
            video = new VideoInformationModel();
        }
        video.setMediaType(mediaType);
        video.generateData();
        return video;
    }

    public static class Trailer implements Parcelable {
        private String key;
        private String site;
        private String type;

        public Trailer() {
            this(Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK);
        }

        public Trailer(String key, String site, String type) {
            this.key = key;
            this.site = site;
            this.type = type;
        }

        protected Trailer(Parcel in) {
            key = in.readString();
            site = in.readString();
            type = in.readString();
        }

        public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
            @Override
            public Trailer createFromParcel(Parcel in) {
                return new Trailer(in);
            }

            @Override
            public Trailer[] newArray(int size) {
                return new Trailer[size];
            }
        };

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Video{" +
                    "key='" + key + '\'' +
                    ", site='" + site + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(key);
            dest.writeString(site);
            dest.writeString(type);
        }
    }

    public static class Trailers implements Parcelable {
        @SerializedName("results")
        private List<Trailer> trailers;

        public Trailers() {
            this(new ArrayList<>());
        }

        public Trailers(List<Trailer> trailers) {
            this.trailers = trailers;
        }

        protected Trailers(Parcel in) {
            trailers = in.createTypedArrayList(Trailer.CREATOR);
        }

        public static final Creator<Trailers> CREATOR = new Creator<Trailers>() {
            @Override
            public Trailers createFromParcel(Parcel in) {
                return new Trailers(in);
            }

            @Override
            public Trailers[] newArray(int size) {
                return new Trailers[size];
            }
        };

        public List<Trailer> getTrailers() {
            return trailers;
        }

        public void setTrailers(List<Trailer> trailers) {
            this.trailers = trailers;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(trailers);
        }
    }

    public static class AlternativeTitle implements Parcelable {
        @SerializedName("iso_3166_1")
        private String ios3166_1;
        private String title;

        public AlternativeTitle() {
            this(Constant.BASE_BLANK, Constant.BASE_BLANK);
        }

        public AlternativeTitle(String ios3166_1, String title) {
            this.ios3166_1 = ios3166_1;
            this.title = title;
        }

        protected AlternativeTitle(Parcel in) {
            ios3166_1 = in.readString();
            title = in.readString();
        }

        public static final Creator<AlternativeTitle> CREATOR = new Creator<AlternativeTitle>() {
            @Override
            public AlternativeTitle createFromParcel(Parcel in) {
                return new AlternativeTitle(in);
            }

            @Override
            public AlternativeTitle[] newArray(int size) {
                return new AlternativeTitle[size];
            }
        };

        public String getIos3166_1() {
            return ios3166_1;
        }

        public void setIos3166_1(String ios3166_1) {
            this.ios3166_1 = ios3166_1;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String site) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "Video{" +
                    "ios3166_1='" + ios3166_1 + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ios3166_1);
            dest.writeString(title);
        }
    }

    public static class AlternativeTitles implements Parcelable {
        @SerializedName(value="name", alternate={"titles", "results"})
        private List<AlternativeTitle> alternativeTitles;

        public AlternativeTitles() {
            this(new ArrayList<>());
        }

        public AlternativeTitles(List<AlternativeTitle> alternativeTitles) {
            this.alternativeTitles = alternativeTitles;
        }

        protected AlternativeTitles(Parcel in) {
            alternativeTitles = in.createTypedArrayList(AlternativeTitle.CREATOR);
        }

        public static final Creator<AlternativeTitles> CREATOR = new Creator<AlternativeTitles>() {
            @Override
            public AlternativeTitles createFromParcel(Parcel in) {
                return new AlternativeTitles(in);
            }

            @Override
            public AlternativeTitles[] newArray(int size) {
                return new AlternativeTitles[size];
            }
        };

        public List<AlternativeTitle> getVideos() {
            return alternativeTitles;
        }

        public void setVideos(List<AlternativeTitle> videos) {
            this.alternativeTitles = alternativeTitles;
        }

        public String getTitle(String languageIso) {
            if (alternativeTitles == null || alternativeTitles.isEmpty())
                return null;
            for (AlternativeTitle item : alternativeTitles) {
                if (item.getIos3166_1().equalsIgnoreCase(languageIso)) {
                    return item.getTitle();
                }
            }
            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(alternativeTitles);
        }
    }
}
