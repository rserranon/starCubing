package Tables;

import fileParser.CsVParser;

import java.util.*;

/**
 * Created by susha on 3/18/2016.
 */
public class Table {
    public List<TableTuple> relationalTable;
    public Tuple key;//has one extra column count

    public Table() {
        relationalTable = new ArrayList<TableTuple>();
        key = new Tuple();
    }

    public void add(Tuple tup) {
        int index = relationalTable.indexOf(tup);

        if (index > 0) {
            relationalTable.get(index).increment();
        } else {
            TableTuple newTT = new TableTuple(tup);
            relationalTable.add(newTT);
        }

    }

    public int getCount(Tuple tup) {
        return relationalTable.get(relationalTable.indexOf(tup)).getTupleCount();
    }

    public void populate(CsVParser parser) {
        key.add(parser.next()); // Add next record to key (tuple Object)
        System.out.println("Key population: " + key.toString());
        List<String> oldOrder = new ArrayList<String>(key.tuplet);

        Collections.sort(key.tuplet); // Sort dimensions(alphabetically?)
        System.out.println("Key population (alpahbetically ordered): " + key.tuplet.toString());
        
        
        int[] sortHelper = new int[key.tuplet.size()];//stores the new location of this dimension
        for (String dimen : oldOrder
                ) {
            sortHelper[oldOrder.indexOf(dimen)] = key.tuplet.indexOf(dimen);
        }
        System.out.println("baseCuboidTable (Ordered Tuples...)"); // Ordered tuples
        while (parser.hasNext()) {
            TableTuple newRow = new TableTuple(parser.next(), sortHelper);
            relationalTable.add(newRow);
            newRow.increment();//make count to 1
            
            System.out.println("Tuple: " + newRow.tuple.toString() + ", count()->" + newRow.getTupleCount());
            // newRow.printHash();
        }
        System.out.println("Done baseCuboidTable (Ordered Tuples...)"); // Ordered tuples
    }

    public StarTable getStarTable(String dimension, int min_sup) {
        StarTable starTable = new StarTable(dimension);
        int index = key.indexOf(dimension);
        for (int i = 0; i < relationalTable.size(); i++) {
            starTable.insert(relationalTable.get(i).get(index));
        }

        starTable.validateStar(min_sup);
        return starTable;
    }

    public List<TableTuple> compress(List<StarTable> starTables) {
        //mark stars
        for (int i = 0; i < relationalTable.size(); i++) {
            for (int j = 0; j < key.size(); j++) {
                if (starTables.get(j).isStar(relationalTable.get(i).get(j))) {
                    relationalTable.get(i).star(j, key.get(j));

                }
            }
        }
        //remove duplicate rows
        Set<TableTuple> uniqueSet = new HashSet<TableTuple>(relationalTable);
        List<TableTuple> newtable = new ArrayList<TableTuple>(uniqueSet);
        for (TableTuple tt : uniqueSet) {
            newtable.get(newtable.indexOf(tt)).setTupleCount(Collections.frequency(relationalTable, tt));
            System.out.println("Compressed tuple" + tt.tuple.tuplet);
        }
        return newtable;
    }
}

