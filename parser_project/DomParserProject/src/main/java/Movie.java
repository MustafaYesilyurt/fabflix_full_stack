import java.util.ArrayList;

public class Movie {

    private String xmlid;
    private String movieId;
    private String title;
    private String year;
    private String director;
    private String directorId;
    private ArrayList<String> genres;

    public Movie(){}

    public Movie(String id, String title, String year, String director, String directorId, ArrayList<String> genres) {
        this.xmlid = id;
        this.movieId = null;
        this.title = title;
        this.year = year;
        this.director = director;
        this.directorId = directorId;
        this.genres = genres;
    }

    public String getXmlId() { return xmlid; }

    public String getMovieId() { return movieId; }

    public String getDirector() {
        return director;
    }

    public String getDirectorId() {
        return directorId;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public ArrayList<String> getGenres() {return genres;}

    public void setXmlId(String id) { this.xmlid = id; }

    public void setMovieId(String movieId) { this.movieId = movieId; }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setDirectorId(String directorId) { this.directorId = directorId; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("ID: " + getXmlId());
        sb.append(", ");
        sb.append("Title: " + getTitle());
        sb.append(", ");
        sb.append("Year: " + getYear());
        sb.append(", ");
        for (int i = 0; i < genres.size(); i++) {
            if (i == 0)
                sb.append("Genres: ");
            sb.append(genres.get(i));
            sb.append(", ");
        }
        sb.append("Director: " + getDirector());
        sb.append(", ");
        sb.append("Director ID: "+ getDirectorId());

        return sb.toString();
    }
}