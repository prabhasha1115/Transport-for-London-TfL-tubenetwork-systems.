import java.io.File;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Console {

    private static final String[] station_list = {"PADDINGTON", "EDGWARE ROAD", "BAKER STREET", "GREAT PORTLAND STREET",
            "EUSTON SQUARE", "KINGS CROSS ST PANCRAS", "FARRINGDON", "BARBICAN", "MOORGATE", "LIVERPOOL STREET", "ALDGATE",
            "TOWER HILL", "MONUMENT", "CANNON STREET", "MANSION HOUSE", "BLACKFRIARS", "TEMPLE", "EMBANKMENT", "WESTMINSTER",
            "ST JAMES PARK", "VICTORIA", "SLOANE SQUARE", "SOUTH KENSINGTON", "GLOUCESTER ROAD", "HIGH STREET KENSINGTON",
            "NOTTING HILL GATE", "BAYSWATER", "REGENTS PARK", "OXFORD CIRCUS", "PICCADILLY CIRCUS", "CHARING CROSS", "QUEENSWAY",
            "LANCASTER GATE", "MARBLE ARCH", "BOND STREET", "TOTTENHAM COURT ROAD", "HOLBORN", "CHANCERY LANE", "ST PAULS", "BANK"};

    private static final int NO_PARENT = -1;

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    // Function that implements bg.Dijkstra's shortest path algorithm
    private static void dijkstra(double[][] adjacency_matrix, int start_vertex, int end_vertex) {

        int n_vertices = adjacency_matrix[0].length;

        // shortest_distances[i] will hold shortest distance from src to an [i]
        double[] shortest_distances = new double[n_vertices];

        // added[i] will hold true if vertex [i] is included
        boolean[] added = new boolean[n_vertices];

        // Initialize all distances in a INFINITE position and added[i] as false value
        for (int vertex_index = 0; vertex_index < n_vertices; vertex_index++) {
            shortest_distances[vertex_index] = Integer.MAX_VALUE;
            added[vertex_index] = false;
        }

        // Distance to source vertex from itself is always zero
        shortest_distances[start_vertex] = 0;

        // Parent array to store the shortest path tree values
        int[] parents = new int[n_vertices];

        // The source vertex doesn't have a parent
        parents[start_vertex] = NO_PARENT;

        // Find shortest path for all vertices
        for (int i = 1; i < n_vertices; i++) {
            // Picking the minimum distance vertex from the set of vertices not yet completed. nearestVertex is always similar to startNode in first loop
            int nearest_vertex = -1;

            double shortest_distance = Integer.MAX_VALUE;

            for (int vertex_index = 0; vertex_index < n_vertices; vertex_index++) {

                if (!added[vertex_index] && shortest_distances[vertex_index] < shortest_distance) {
                    nearest_vertex = vertex_index;
                    shortest_distance = shortest_distances[vertex_index];
                }
            }

            // flag the used vertex as true
            added[nearest_vertex] = true;

            // Update the distance value of the adjacent vertices of the used vertex
            for (int vertex_index = 0; vertex_index < n_vertices; vertex_index++) {

                double edge_distance = adjacency_matrix[nearest_vertex][vertex_index];

                if (edge_distance > 0 && ((shortest_distance + edge_distance) < shortest_distances[vertex_index])) {
                    parents[vertex_index] = nearest_vertex;
                    shortest_distances[vertex_index] = shortest_distance + edge_distance;
                }
            }
        }

        print_solution(adjacency_matrix, start_vertex, end_vertex, shortest_distances, parents);
    }

    // Function to output the constructed distances array and shortest paths to user
    private static void print_solution(double[][] adjacencyMatrix, int startVertex, int endVertex, double[] distances, int[] parents) {

        int nVertices = distances.length;

        for (int vIndex = 0; vIndex < nVertices; vIndex++) {

            if (vIndex != startVertex && vIndex == endVertex) {
                decimalFormat.setRoundingMode(RoundingMode.UP);
                System.out.println("Route: " + station_list[startVertex] + " ---> " + station_list[vIndex]);
                printPath(adjacencyMatrix, vIndex, parents);
                System.out.println("Total Time Spent: " + decimalFormat.format(distances[vIndex]) + "\t\t");
            }
        }
    }

    // Function to print shortest path from source to current vertex using parents array values
    private static int previousIndex;
    private static boolean checkedFlag;

    private static void printPath(double[][] adjacencyMatrix, int currentVertex, int[] parents) {

        if (currentVertex == NO_PARENT) {
            return;
        }

        printPath(adjacencyMatrix, parents[currentVertex], parents);

        if (checkedFlag) {
            System.out.println("\t" + station_list[previousIndex] + " ---> " + station_list[currentVertex] + " = " + adjacencyMatrix[previousIndex][currentVertex]);
        }
        else {
            checkedFlag = true;
        }

        previousIndex = currentVertex;
    }


    public static void main(String[] args) {

        double[][] graph = readingFromFile();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Start: ");
        String startStation = scanner.nextLine();

        System.out.print("Enter End: ");
        String endStation = scanner.nextLine();

        int start = 0, end = 0;
        boolean startCheck = false, endCheck = false;

        for (int station = 0; station < station_list.length; station++) {
            if (station_list[station].equalsIgnoreCase(startStation)) {
                start = station;
                startCheck = true;
            }
            else if (station_list[station].equalsIgnoreCase(endStation)) {
                end = station;
                endCheck = true;
            }
        }

        if (Objects.equals(startStation, endStation)) {
            System.out.println("Please try again with a different station!");
        }
        else if (!startCheck || !endCheck) {
            System.out.println("Please try again with a valid station!");
        }
        else if (graph != null) {
            dijkstra(graph, start, end);
        }
    }

    private static double[][] readingFromFile() {

        ArrayList<String> data_input = new ArrayList<>();

        try {
            File myObj = new File("benchmarks/cw.txt"); // getting input file
            System.out.println("Reading data from the file . . . .");
            Scanner scanner = new Scanner(myObj);

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                data_input.add(data);
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error!!!, File not available.");
        }
        if (data_input.size() != 0) {
            return generateGraph(data_input);
        } else {
            System.out.println("Error!!!, File is empty");
            return null;
        }
    }

    public static double[][] generateGraph(ArrayList<String> inputData) {
        // Generating the graph to display
        int graph_size = Integer.parseInt(inputData.get(0).trim());
        double[][] matrixGraph = new double[graph_size][graph_size];
        System.out.println("Creating Adjacent Matrix Graph . . . .");

        for (int item = 1; item < inputData.size(); item++) {
            String[] splitItems = inputData.get(item).split(" ");
            int x = Integer.parseInt(splitItems[0].trim());
            int y = Integer.parseInt(splitItems[1].trim());
            double capacity = Double.parseDouble(splitItems[2].trim());

            addEdges(x, y, capacity, matrixGraph);
        }
        return matrixGraph;
    }

    private static void addEdges(int x, int y, double capacity, double[][] graph) {
        graph[x][y] = capacity;
    }

}

