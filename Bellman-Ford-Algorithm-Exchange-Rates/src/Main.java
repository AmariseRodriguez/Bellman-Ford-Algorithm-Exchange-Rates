import java.util.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        String csvFile = "/Users/amariserodriguez/Library/Bellman-Ford-Algorithm_Exchange-Rates/src/exchange rates.csv";
        CurrencyExchange ce = new CurrencyExchange(csvFile);

        try(Scanner sc = new Scanner(System.in)) {
            System.out.println("Please enter the source currency: ");
            String sourceCurrency = sc.nextLine();
            ce.calculate(sourceCurrency);
        }
    }
}

class BellmanFord {
    class Edge {
        int src, dest;
        double weight;
        Edge() {
            src = 0;
            dest = 0;
            weight = 0;
        }
    }

    int V, E;
    Edge edge[];

    BellmanFord(int v, int e) {
        V = v;
        E = e;
        edge = new Edge[e];
        for(int i = 0; i < e; ++i)
            edge[i] = new Edge();
    }

    void addEdge(int i, int src, int dest, double weight) {
        edge[i].src = src;
        edge[i].dest = dest;
        edge[i].weight = -Math.log(weight);
    }

    void BellmanFordAlgo(CurrencyExchange currencyExchange, int src, double[] directRates) {
        double dist[] = new double[V];
        for(int i = 0; i < V; ++i)
            dist[i] = Double.MAX_VALUE;
        dist[src] = 0;

        for(int i = 1; i < V; ++i) {
            for(int j = 0; j < E; ++j) {
                int u = edge[j].src;
                int v = edge[j].dest;
                double weight = edge[j].weight;
                if (dist[u] != Double.MAX_VALUE && dist[u] + weight < dist[v])
                    dist[v] = dist[u] + weight;
            }
        }

        for (int j = 0; j < E; ++j) {
            int u = edge[j].src;
            int v = edge[j].dest;
            double weight = edge[j].weight;

            if(dist[u] != Double.MAX_VALUE && dist[u] + weight < dist[v]) {
                System.out.println("Graph contains negative weight cycle");
                return;
            }
        }
        printArr(dist, src, currencyExchange.getIndexCurrencyMap(),directRates);
    }
    void printArr(double dist[], int V, List<String> currencies, double[] directRates) {
        System.out.println("Source currency is " + currencies.get(V));

        for(int i = 0; i < dist.length; ++i) {
            if(i != V)
                System.out.printf("%s: Max Exchange Rate is %f, and Direct Rate is %f\n", currencies.get(i), Math.exp(-dist[i]), directRates[i]);
        }
    }
}

class CurrencyExchange {
    private BellmanFord graph;
    private Map<String, Integer>currencyIndexMap;
    private List<String> indexCurrencyMap;
    public CurrencyExchange(String csvFile) {
        String line;
        String cvsSplitBy =",";
        List<String[]> rates = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] currencyRates = line.split(cvsSplitBy);
                rates.add(currencyRates);
            }

            int numOfCurrencies = rates.get(0).length - 1;
            graph = new BellmanFord(numOfCurrencies, numOfCurrencies*numOfCurrencies);
            currencyIndexMap = new HashMap<>();
            indexCurrencyMap = new ArrayList<>();

            for(int i = 1; i <= numOfCurrencies; i++) {
                currencyIndexMap.put(rates.get(i)[0], i-1);
                indexCurrencyMap.add(rates.get(i)[0]);

                for(int j = 1; j <= numOfCurrencies; j++) {
                    double rate = Double.parseDouble(rates.get(i)[j]);
                    graph.addEdge((i-1) *numOfCurrencies + (j-1),i-1, j-1, rate);
                }
            }

        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void calculate(String sourceCurrency) {
        double[] directRates = new double[indexCurrencyMap.size()];
        for(int i = 0; i < directRates.length; i++){
            directRates[i] = Math.exp(-graph.edge[currencyIndexMap.get(sourceCurrency) *indexCurrencyMap.size() + i].weight);
        }
        graph.BellmanFordAlgo(this, currencyIndexMap.get(sourceCurrency), directRates);
    }
    public List<String> getIndexCurrencyMap() {
        return indexCurrencyMap;
    }
}
