# Spotify

`Spotify` е финалния курсов проект за курса "Съвременни Java технологии 2025/26" 

## Въведение

Системата е изградена на базата на Клиент-Сървър комуникация, като потребителя подава командите си през конзолата чрез `command line 
interface`. Също така се предоставя възможност за периодично запазване на логове за действието на системата.

### Основните команди са:
```bash
register <email> <password> -> позволява потребител да се регистрира в системата
login <email> <password> -> позволява потребител да се впише в системата с вече съществуващ профил
disconnect -> потребител излиза от системата
search <words> -> търсене по една или повече ключови думи в името/певеца на песен
top <number> -> позава първите "number" на брой песни по брой слушания, в случай на равенство - по азбучен ред
create-playlist <name_of_the_playlist> -> позволява създаване на нов плейлист
add-song-to <name_of_the_playlist> <song> -> позволява добавяне на песен към плейлист, но само от собственика му
show-playlist <name_of_the_playlist> -> показва всички песни в плейлист
play <song> -> пуска песен
stop -> спира песен
```

## Реализация

### Клиент
Класът `SpotifyClient` представя основната реализация на клиентската част на системата. Има публичен конструктор, който приема порт, към 
който да се свърже, създава нов MusicPlayer и задава текущия log-нат потребител на null.
```java
public SpotifyClient(int port) {
        this.port = port;
        this.user = null;
        this.musicPlayer = new MusicPlayer();
    }
```

#### MusicPlayer
Всеки клиент има `MusicPlayer`, който се грижи за слушането на песните
```java
public class MusicPlayer {
    private final ExecutorService player;
    private Future<?> currentTask;
    private PlayedSong currentSong;

    public MusicPlayer() {
        this.player = Executors.newSingleThreadExecutor();
        this.currentSong = null;
    }

    public void play(String url) {
        if (currentTask != null && !currentTask.isDone()) {
            currentSong.stop();
        }

        currentSong = new PlayedSong(url);
        currentTask = player.submit(currentSong);
    }

    public void stop() {
        if (currentSong != null) {
            currentSong.stop();
            currentSong = null;
            currentTask = null;
        }
    }

    public void disconnect() {
        stop();
        player.shutdown();
    }
}
```
Тъй като не искаме слушането на музика да прекъсва останалта част от програмата, слушането се извършва на отделна нишка

#### PlayedSong
Класът `PlayedSong` е Runnable и отговаря за самото stream-ване на песен, което се управлява от `MusicPlayer`
```java
public class PlayedSong implements Runnable {
    private static final int BUFFER_SIZE = 1024;
    private final String url;
    private boolean isPlaying;

    public PlayedSong(String url) {
        this.url = url;
        this.isPlaying = true;
    }

    @Override
    public void run() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(this.url));
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open();
            dataLine.start();

            byte[] bufferBytes = new byte[BUFFER_SIZE];
            int readBytes;
            while (isPlaying && (readBytes = audioInputStream.read(bufferBytes)) != -1) {
                dataLine.write(bufferBytes, 0, readBytes);
            }

            dataLine.drain();
            dataLine.stop();
            dataLine.close();

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("Could not play song");
        }
        isPlaying = false;
    }

    public void stop() {
        this.isPlaying = false;
    }
}
```

### Сървър
`SpotifyServer` представлява основната имплементация на сървърната част на системата
Има публичен конструктор `public SpotifyServer(int port, UserManager userManager, SongManager songManager)`, който задава порта, на 
който ще се работи, както и инстации на помощните класове `UserManager` и `SongManager`

Също така, сървърът има няколко имплементации на `LogManager`, който контролнира раобтата с базата данни/записването на логове


#### LogManager
```java
public abstract class DatabaseWriter<T> {
    private static final int THREAD_COUNT = 1;
    private static final int INITIAL_DELAY = 10;
    private static final int DEFAULT_DELAY = 20;

    protected final ScheduledExecutorService scheduler  = Executors.newScheduledThreadPool(THREAD_COUNT);
    protected final Logger<T> logger;

    public DatabaseWriter(Logger<T> logger) {
        this.logger = logger;
        scheduler.scheduleWithFixedDelay(this::write, INITIAL_DELAY, DEFAULT_DELAY, TimeUnit.SECONDS);
    }

    public DatabaseWriter(Logger<T> logger, int delay) {
        this.logger = logger;
        scheduler.scheduleWithFixedDelay(this::write, INITIAL_DELAY, delay, TimeUnit.SECONDS);
    }

    abstract void write();

    public void shutdown() {
        this.scheduler.shutdown();
    }
}
```

`LogManager` наследява абстрактния `DatabaseWriter`, който дава основна абстракция за динамично управление на файловете, служещи за 
базата данни. Към момента единствено `LogManager` го наследва, но той позволяа евентуално бъдещо разширение на системата.

```java
public class LogManager<T> extends DatabaseWriter<T> {
    private final Queue<T> logsToProcess;

    public LogManager(Logger<T> logger) {
        this.logsToProcess = new LinkedList<>();

        super(logger);
    }

    public LogManager(Logger<T> logger, int delay) {
        this.logsToProcess = new LinkedList<>();

        super(logger, delay);
    }

    public void add(T log) {
        logsToProcess.add(log);
    }

    void write() {
        while (!logsToProcess.isEmpty()) {
            T toBeLogged = logsToProcess.peek();
            try {
                logger.log(toBeLogged);
                logsToProcess.poll();
            } catch (IOException e) {
                break;
            }
        }
    }
}
```
`LogManager` е изграден като generic клас, който има `Queue` с чакащи да бъдат логнати съобщения. Това логване става периодично от ScheduledExecutorService scheduler

Всеки `LogManager` си има `Logger`, който следи за самото записване във файл
```java
public abstract class Logger<T> {
    protected final BufferedWriter writer;

    public Logger(File file, boolean append) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(file, append));
    }

    public abstract void log(T toBeLogged) throws IOException;
}
```

#### SongManager
Това е основния клас, който менажира работата с всички песни и плейлисти.
```java
private final Map<String, Song> allSongs;
private final Map<String, Playlist> allPlaylists;

public SongManager() {
    this.allSongs = new HashMap<>();
    this.allPlaylists = new HashMap<>();
}
```
Песните и плейлистите пази под формата на `Map` с ключ името на самата песен/плейлист. Основните функционалности се реализират чрез 
методите 
```java
public void createSong(Song song);
    
public void createPlaylist(Playlist playlist);

public List<String> search(String[] keywords);

public List<String> top(int topSongs);

public void addSongToPlaylist(String playlistName, String songName, String userEmail);

public List<String> showPlaylist(String playlistName);

public Song playSong(String songName);

public String getPlaylistOwner(String playlistName);
```

#### Playlist
Представлява един единствен плейлист, който се характеризира с име, собственик и `Map` от песните в плейлиста
```java
private final String name;
private final Map<String, Song> songs;
private final String ownerEmail;

public Playlist(String name, String userEmail) {
    this.name = name;
    songs = new HashMap<>();
    this.ownerEmail = userEmail;
}
```

#### Song
Представлява една единствена песен, която се характеризира с име, певец, url за пускане и общо пускания
```java
private final String name;
private final String artist;
private final String url;
private int totalPlays;

public Song(String name, String artist, String url, int totalPlays) {
    this.artist = artist;
    this.name = name;
    this.url = url;
    this.totalPlays = totalPlays;
}
```

#### UserManager
Това е основния клас, който менажира работата с всички потребители.
```java
private final Map<String, User> users;

public UserManager() {
    this.users = new HashMap<>();
}
```
Потребителите пази под формата на `Map` с ключ имейла на потребителя.

#### User
`Record`, който пази имейл и парола на потребител
```java
public record User(String email, String password){}
```

### Комуникация
Заявка от страна на клиента:
```java
public record ClientQuery(String email, String command) {

    public static ClientQuery of(String email, String command) {
        return new ClientQuery(email, command);
    }
}
```
Отговор на сървъра:
```java
public record ServerResponse(ResponseStatus status, String message, String logMessage, User user,
                             Object additionalInfo) {
    public static ServerResponse of(ResponseStatus status,
                                    String message,
                                    String logMessage,
                                    User user) {
        return new ServerResponse(status, message, logMessage, user, null);
    }

    public static ServerResponse of(ResponseStatus status,
                                    String message,
                                    String logMessage,
                                    User user,
                                    Object additionalInfo) {
        return new ServerResponse(status, message, logMessage, user, additionalInfo);
    }
}
```
```java
public enum ResponseStatus {
    DEFAULT_SUCCESS,
    REGISTRATION,
    CREATE_PLAYLIST,
    ADD_SONG,
    PLAY,
    STOP,
    DISCONNECT,
    FAILURE
}
```