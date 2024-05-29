package regressions;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Rich
 */
public class LogisticRegression {
    double [] x [];
    double y [];
    public LogisticRegression(Dataset dataset){
        x = dataset.getX();
        y = dataset.gety();
        //Arrays.fill(w, 0);
        //w[j] = w[j] + learningRate * error * x_i[j];

    }

    double [] predict(double [] vectorOfBetas) {
        double [] prediction = new double [x[0].length];
        for (int i = 0; i < x[0].length; i++) {
            for (int j = 0; j < vectorOfBetas.length; j++){
                prediction[i]+= vectorOfBetas[j]*x[j][i];
            }
            prediction[i] = 1.0 / (1.0 + Math.exp(-prediction[i]));
        }
        return prediction;
    }

    public double [] fit(double precision, double learningRate, int maxIterations){
        double w [] = new double [x.length];
        int iterations = 0;
        double [] error;
        do{
            double [] p_i = predict(w);
            error = substractVector(p_i, y);
            for(int j = 0; j < x.length; j++){
                w[j] = w[j] + learningRate * sumVector(multiplyVector(error, x[j]));
            }
            iterations++;
        }while(sumVector(error) > precision && iterations < maxIterations);
        return w;
    }

    double [] substractVector(double [] a, double [] b) {
        double [] result = new double [a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }
    double [] multiplyVector(double [] a, double [] b) {
        double [] result = new double [a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * b[i];
        }
        return result;
    }
    double sumVector(double [] a) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i];
        }
        return result;
    }
}
