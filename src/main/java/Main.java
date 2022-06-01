import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameJson = "data.json";
        String fileNameJson2 = "data2.json";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String csvJson = listToJson(list);
        writeString(csvJson, fileNameJson);

        List<Employee> list2 = parseXML("data.xml");
        String xmlJson = listToJson(list2);
        writeString(xmlJson, fileNameJson2);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        System.out.println(gson.toJson(list));
        return json;
    }

    public static void writeString(String json, String fileNameJson) {
        try (FileWriter file = new FileWriter(fileNameJson)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> staff = new ArrayList<>();
        try {
            File file = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("employee");
            System.out.println("Information of all employees");

            for (int s = 0; s < nodeList.getLength(); s++) {
                Node node = nodeList.item(s);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
                    String[] names = {"ID", "FirstName", "LastName", "Country", "Age"};
                    for (int i = 0; i < columnMapping.length; i++) {
                        NodeList nodeList1 = element.getElementsByTagName(columnMapping[i]);
                        Element element1 = (Element) nodeList1.item(0);
                        NodeList nodeList2 = element1.getChildNodes();
                        names[i] = nodeList2.item(0).getNodeValue();
                        System.out.println(columnMapping[i] + " : " + names[i]);
                    }
                    Employee employee = new Employee(Long.parseLong(names[0]), names[1], names[2], names[3], Integer.parseInt(names[4]));
                    staff.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staff;
    }
}
