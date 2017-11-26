package hr.wozai.service.nlp.server.util.segment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class WordDictionary {

    private Logger logger = LoggerFactory.getLogger(WordDictionary.class);

    private static WordDictionary singleton;
    private static final String MAIN_DICT = "/segment/dict.txt";
    private static String USER_DICT_SUFFIX = ".dict";
    private static final String USER_DICT = "/segment/user.dict";

    public final Map<String, Double> freqs = new HashMap<String, Double>();
    public final Set<String> loadedPath = new HashSet<String>();
    private Double minFreq = Double.MAX_VALUE;
    private Double total = 0.0;
    private DictSegment _dict;


    private WordDictionary() {
        this.loadDict();
        this.loadUserDict();
    }


    public static WordDictionary getInstance() {
        if (singleton == null) {
            synchronized (WordDictionary.class) {
                if (singleton == null) {
                    singleton = new WordDictionary();
                    return singleton;
                }
            }
        }
        return singleton;
    }


//    /**
//     * for ES to initialize the user dictionary.
//     *
//     * @param configFile
//     */
//    public void init(Path configFile) {
//        String abspath = configFile.toAbsolutePath().toString();
//        this.logger.info("initialize user dictionary:" + abspath);
//        synchronized (WordDictionary.class) {
//            if (loadedPath.contains(abspath))
//                return;
//
//            DirectoryStream<Path> stream;
//            try {
//                stream = Files.newDirectoryStream(configFile, String.format(Locale.getDefault(), "*%s", USER_DICT_SUFFIX));
//                for (Path path: stream){
//                    this.logger.error(String.format(Locale.getDefault(), "loading dict %s", path.toString()));
//                    singleton.loadUserDict();
//                }
//                loadedPath.add(abspath);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                // e.printStackTrace();
//                this.logger.error(String.format(Locale.getDefault(), "%s: load user dict failure!", configFile.toString()));
//            }
//        }
//    }
    
    
    /**
     * let user just use their own dict instead of the default dict
     */
    public void resetDict(){
    	_dict = new DictSegment((char) 0);
    	freqs.clear();
    }


    public void loadDict() {
        _dict = new DictSegment((char) 0);
        InputStream is = this.getClass().getResourceAsStream(MAIN_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");

                if (tokens.length < 2)
                    continue;

                String word = tokens[0];
                double freq = Double.valueOf(tokens[1]);
                total += freq;
                word = addWord(word);
                freqs.put(word, freq);
            }
            // normalize
            for (Entry<String, Double> entry : freqs.entrySet()) {
                entry.setValue((Math.log(entry.getValue() / total)));
                minFreq = Math.min(entry.getValue(), minFreq);
            }
            this.logger.info(String.format(Locale.getDefault(), "main dict load finished, time elapsed %d ms",
                System.currentTimeMillis() - s));
        }
        catch (IOException e) {
            this.logger.error(String.format(Locale.getDefault(), "%s load failure!", MAIN_DICT));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                this.logger.error(String.format(Locale.getDefault(), "%s close failure!", MAIN_DICT));
            }
        }
    }


    private String addWord(String word) {
        if (null != word && !"".equals(word.trim())) {
            String key = word.trim().toLowerCase(Locale.getDefault());
            _dict.fillSegment(key.toCharArray());
            return key;
        }
        else
            return null;
    }


    public void loadUserDict() {
        loadUserDict(StandardCharsets.UTF_8);
    }


    public void loadUserDict(Charset charset) {
        InputStream is = this.getClass().getResourceAsStream(USER_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
            long s = System.currentTimeMillis();
            int count = 0;
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");

                if (tokens.length < 1) {
                    // Ignore empty line
                    continue;
                }

                String word = tokens[0];

                double freq = 3.0d;
                if (tokens.length == 2)
                    freq = Double.valueOf(tokens[1]);
                word = addWord(word); 
                freqs.put(word, Math.log(freq / total));
                count++;
            }
            this.logger.info(String.format(Locale.getDefault(), "user dict %s load finished, tot words:%d, time elapsed:%dms", USER_DICT.toString(), count, System.currentTimeMillis() - s));
            br.close();
        }
        catch (IOException e) {
            this.logger.error(String.format(Locale.getDefault(), "%s: load user dict failure!", USER_DICT.toString()));
        }
    }


    public DictSegment getTrie() {
        return this._dict;
    }


    public boolean containsWord(String word) {
        return freqs.containsKey(word);
    }


    public Double getFreq(String key) {
        if (containsWord(key))
            return freqs.get(key);
        else
            return minFreq;
    }
}
