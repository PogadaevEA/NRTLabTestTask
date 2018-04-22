package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class HTMLParser {
    public static void main(String[] args) throws Exception{
        System.out.println("Enter space-separated terms to search:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String stringSearch = reader.readLine();

       List resultSearch = searchByWords(stringSearch);

        Iterator<Integer> iter = resultSearch.iterator();
        while(iter.hasNext()){
            System.out.println(iter.next());
        }
    }

    public static List searchByWords(String stringSearch){

        String[] wordsSearch = stringSearch.split(" ");
        TreeMap<String, Integer> resultMap = new TreeMap<>();

        for (String str: wordsSearch){
            resultMap.put(str,0);
        }
        //Parsing HTML pages https://news.yandex.ru/computers.html
        Document doc;
        StringBuilder resultTextSB = new StringBuilder();

        try {
            doc = Jsoup.connect("https://news.yandex.ru/computers.html").get();

            Elements search = doc.select("label.input__hint");
            Elements tabMenu = doc.select("li.nav-by-rubrics__rubric");
            Elements rubric = doc.select("div.rubric-label");
            Elements storyTitle = doc.select("h2.story__title");
            Elements storyText = doc.select("div.story__text");
            Elements storyDate = doc.select("div.story__date");
            Elements moreStories = doc.select("div.more__rubric-stories");
            Elements autoUpdate = doc.select("div.auto-update");
            Elements rubberCell = doc.select("div.rubber__cell");

            resultTextSB.append(search.text()).append(" ");
            resultTextSB.append(tabMenu.text()).append(" ");
            resultTextSB.append(rubric.text()).append(" ");
            resultTextSB.append(storyTitle.text()).append(" ");
            resultTextSB.append(storyText.text()).append(" ");
            resultTextSB.append(storyDate.text()).append(" ");
            resultTextSB.append(moreStories.text()).append(" ");
            resultTextSB.append(autoUpdate.text()).append(" ");
            resultTextSB.append(rubberCell.text());

        } catch (IOException e){
            e.printStackTrace();
        }

        String resultText = resultTextSB.toString();

        String[] searchString = resultText.replaceAll("\\pP", "").split(" ");
        ArrayList<String> searchArray = new ArrayList<>();

        for (String str: searchString){
            searchArray.add(str);
        }

        //Find the number of repetitions
        for (Map.Entry<String,Integer> pair : resultMap.entrySet()){
            int count = pair.getValue();
            for (int i = 0; i < searchArray.size();i++){
                if(pair.getKey().equalsIgnoreCase(searchArray.get(i)))
                    count++;
            }
            pair.setValue(count);
        }

        List list = new ArrayList(resultMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        //Sort the list by value
        Collections.reverseOrder(resultMap.comparator());

        return list;
    }
}
