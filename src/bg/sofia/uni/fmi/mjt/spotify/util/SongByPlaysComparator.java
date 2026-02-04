package bg.sofia.uni.fmi.mjt.spotify.util;

import bg.sofia.uni.fmi.mjt.spotify.model.song.Song;

import java.util.Comparator;

public class SongByPlaysComparator implements Comparator<Song> {

    @Override
    public int compare(Song lhs, Song rhs) {
        if (lhs.totalPlays() == rhs.totalPlays()) {
            return -lhs.name().compareTo(rhs.name());
        }
        return Integer.compare(lhs.totalPlays(), rhs.totalPlays());
    }
}
