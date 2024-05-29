package regressions;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Dataset implements java.io.Serializable {
    //Benneton default case
    //private double x [] = {23, 26, 30, 34, 43, 48, 52, 57, 58};   // Input
    //private double y [] = {651, 762, 856, 1063, 1190, 1298, 1421, 1440, 1518};   // Output

    private double x1[] = new double[]{41.9, 43.4, 43.9, 44.5, 47.3, 47.5, 47.9, 50.2, 52.8, 53.2, 56.7, 57, 63.5, 65.3, 71.1, 77, 77.8};
    private double x2[] = new double[]{29.1, 29.3, 29.5, 29.7, 29.9, 30.3, 30.5, 30.7, 30.8, 30.9, 31.5, 31.7, 31.9, 32, 32.1, 32.5, 32.9};

    private double x0[] = new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    double[] X[] = {x0, x1, x2};
    double [] x = x1;
    int degreeOfPolynomial=2;
    private double y[] = new double[]{251.3, 251.3, 248.3, 267.5, 273, 276.5, 270.3, 274.9, 285, 290, 297, 302.5, 304.5, 309.3, 321.7, 330.7, 349};

    void Dataset() {
    }
    //Constructor that receives two parameters: the dataset in cvs format, and Type of regression as string
    public Dataset(File DatasetCVS, String type) {
        ArrayList<String> lines = new ArrayList<String>();
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(DatasetCVS))) {
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] data = lines.get(0).split(cvsSplitBy);
        if (type.equals("MLR")||type.equals("LOG")) {
            X = new double[lines.size()][data.length-1];
            y = new double[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                data = lines.get(i).split(cvsSplitBy);
                X[i] = new double[data.length - 1];
                for (int j = 0; j < data.length - 1; j++) {
                    X[i][j] = Double.parseDouble(data[j]);
                }
                y[i] = Double.parseDouble(data[data.length - 1]);
            }
        }
        if (type.equals("SLR")) {
            x = new double[lines.size()];
            y = new double[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                data = lines.get(i).split(cvsSplitBy);
                x[i] = Double.parseDouble(data[0]);
                y[i] = Double.parseDouble(data[1]);
            }
        }
        //POLY dataset comes in the form column x, column y, degree of polynomial
        if (type.equals("POLY")) {
            x = new double[lines.size()];
            y = new double[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                data = lines.get(i).split(cvsSplitBy);
                x[i] = Double.parseDouble(data[0]);
                y[i] = Double.parseDouble(data[1]);
                if(i==0) degreeOfPolynomial = Integer.parseInt(data[2]);
            }
        }

    }

    double[][] getX() {
        return X;
    }
    double[] getx() {
        return x;
    }

    double[] gety() {
        return y;
    }
    int getDegreeOfPolynomial() {
        return degreeOfPolynomial;
    }


}