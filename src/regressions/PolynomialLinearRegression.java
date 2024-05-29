package regressions;

import java.util.Arrays;

public class PolynomialLinearRegression {
    double [][] X;
    double [] x;
    double [][] Y;
    public PolynomialLinearRegression(Dataset dataset){
        x = dataset.getx();
        X = new double[dataset.getx().length][dataset.getDegreeOfPolynomial()+1];
        for (int i = 0; i<dataset.getx().length;i++){
            for (int j = 0; j<dataset.getDegreeOfPolynomial()+1;j++){
                X[i][j] = Math.pow(x[i],j);
            }
        }

        for (double[] array : X) {
            System.out.println(Arrays.toString(array));
        }
        System.out.println(X[0].length);
        System.out.println(X.length);

        Y= new double [dataset.gety().length][1];
        for (int i = 0; i<dataset.gety().length;i++){
            Y[i][0] = dataset.gety()[i];
        }

        //Print on screen values of y
        for (int i = 0; i<dataset.gety().length;i++){
            System.out.println("Y["+i+"][0] = "+Y[i][0]);
        }
    }
    public double [] fit(){
        return MatrixOperations.matrixToVector(MatrixOperations.multiply(MatrixOperations.invert(MatrixOperations.multiply(MatrixOperations.transpose(X),X)),MatrixOperations.multiply(MatrixOperations.transpose(X), Y)));
    }
}
