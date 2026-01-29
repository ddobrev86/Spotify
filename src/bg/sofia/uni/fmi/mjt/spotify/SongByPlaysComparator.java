package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.model.song.Song;

import java.util.Comparator;

public class SongByPlaysComparator implements Comparator<Song> {

    @Override
    public int compare(Song lhs, Song rhs) {
        return Integer.compare(lhs.totalPlays(), rhs.totalPlays());
    }
}
