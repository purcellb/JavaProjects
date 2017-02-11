/**
 * @title NewsBoySimulation
 * @author Bobby Purcell
 * @description Prints to an output file the results of a 1000 day simulation of a paper boy selling papers.
 *      Simulation runs three times total with the paper boy using different ordering methods each run.
 *      Ordering types are as follows:
 *      1- Paper boy orders 16 papers every day
 *      2- Paper boy orders the amount that was demanded the previous day (16 first day)
 *      3- Paper boy orders one less than the amount that was demanded the previous day (16 first day)
 */
package com.NewsBoy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;

public class Main {

    public static void main(String[] args) {

        //Initialize PrintWriter to output the simulation outcome
        PrintWriter output;
        try {
            output = new PrintWriter(new File("NewsBoyOutput.txt"));

            // starting variables
            int day, OrderType, dmd;

            //starting objects
            NewsBoy bob = new NewsBoy();
            CalcStats stats = new CalcStats();
            DemandProcedure dmdProc = new DemandProcedure();

            //formatting util for fancier number output
            DecimalFormat df = new DecimalFormat("0.00");

            //-------------The Simulation loop----------------
            //for each order type, run the simulation loop
            for (OrderType = 1; OrderType <= 3; OrderType++) {
                //the simulation loop runs for 1000 "days", calculates statistics for each day
                for (day = 1; day <= 1000; day++) {
                    dmd = dmdProc.DemandToday();
                    bob.SetDemand(dmd);
                    stats.Calculate(bob.GetProfit());
                    bob.Order(OrderType);
                    bob.Ledger();
                    //prints daily outcomes, testing code
                    //System.out.printf("Demand %d, Order %d, Sold %d, Profit %f \n", bob.demand, bob.GetOrdered(),
                    // bob.GetSold(), bob.profit);
                }
                output.printf("Simulation type %d:\n\tAverage Profit(per day): $%s \tProfit Variance: $%s\n",
                        OrderType, df.format(stats.GetAverage()), df.format(stats.GetVariance()));

                output.printf("\tAverage Papers Sold: %s \t\t\tSales Variance: %s\n\tAverage Papers Demanded: %s" +
                                "\t\tDemand Variance: %s\n", df.format(bob.GetAvgSold()), df.format(bob.GetAvgSoldVar()),
                        df.format(bob.GetAvgDemand()), df.format(bob.GetAvgDemandVar()));
                output.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    //buys and sells papers, orders papers in three different ways dependent upon the OrderType called for
    //keeps a ledger with the average papers sold and demanded each day and their respective variance
    private static class NewsBoy {

        //days paper demand, qty received from order, qty paperboy bought, and qty sold today
        private int demand, ordered, bought, sold;
        //average and variance numbers for the ledger
        private double saverage, daverage;
        private double svariance, dvariance;
        public double sstdev, dstdev;
        private double count;
        //days profit
        private double profit;
        //demand
        private double dsum, dsum2;
        //sales
        private double ssum, ssum2;

        //constructor, def all to zeros except order, order 16 by default
        public NewsBoy() {
            demand = bought = sold = 0;
            ordered = 16;
            profit = 0.0;
        }

        //orders papers
        public int Order(int OrderType) {
            //OrderType changes how Order executes
            int x; //num papers to order
            switch (OrderType) {
                //type 1, order 16 papers every day
                case 1:
                    x = 16;
                    ordered = x;
                    return x;

                //type 2, order the amount bought yesterday
                case 2:
                    if (demand > 0) {//dont return 0 ordered
                        ordered = demand;
                        x = demand;
                    } else {
                        ordered = 17;//if none were demanded or its first run, default to 17
                        x = 17;
                    }
                    return x;
                case 3:
                    if (demand >= 2) { //dont return 0 ordered
                        ordered = demand - 1;
                        x = demand - 1;
                    } else {
                        ordered = 17;//if none were demanded or its first run, default to 17
                        x = 17;
                    }
                    return x;
                default:
                    throw new InvalidParameterException(
                            String.format("Invalid OrderType %d: Must be type 1,2, or 3", OrderType));
            }
        }

        //newsboys daily behavior
        private void Behavior() {
            bought = ordered;
            if (demand >= bought)
                sold = bought;
            else
                sold = demand;
            //profit = 1 dollar for each sale, minus cost plus return value of unsold papers
            profit = (1.00 * sold - .35 * bought) + (.05 * (bought - sold));
        }

        public void Ledger() {
            count++; //count of calls, ergo how many days have run
            ssum += sold;//total papers sold
            ssum2 += sold * sold;//sum of sold squared
            dsum += demand;//total demand
            dsum2 += demand * demand;//total demand squared
            saverage = ssum / count; //avg papers sold
            daverage = dsum / count; //avg demand for papers
            svariance = ssum2 / count - saverage * saverage; //sales variance
            dvariance = dsum2 / count - daverage * daverage; //demand variance
            sstdev = Math.sqrt(svariance); //stdev of the sales
            dstdev = Math.sqrt(dvariance); //stdev of the demand
            return;
        }
        //Newsboy setters and getters

        //set demand, execute behavior for that demand
        public void SetDemand(int x) {
            demand = x;
            Behavior();
        }

        public double GetProfit() {
            return (double) Math.round(profit * 100d) / 100d;
        }

        public int GetSold() {
            return sold;
        }

        public int GetOrdered() {
            return ordered;
        }

        public double GetAvgDemand() {
            return (double) Math.round(daverage * 100d) / 100d;
        }

        public double GetAvgSoldVar() {
            return (double) Math.round(svariance * 100d) / 100d;
        }

        public double GetAvgDemandVar() {
            return (double) Math.round(dvariance * 100d) / 100d;

        }

        public double GetAvgSold() {
            return (double) Math.round(saverage * 100d) / 100d;
        }

    }//end NewsBoy

    //Class CalcStats
    // -calculates statistics for the current day and avgs for the current run
    private static class CalcStats {
        private double profit;
        private double psum;
        private double psum2;
        private double average;
        private double stdev;
        private double variance;
        private int count;

        //const, set everything to zerp
        public CalcStats() {
            profit = psum = psum2 = average = stdev = variance = 0.0;
            count = 0;
        }

        //sets profit to x, calculates the stats and sets them
        public void Calculate(double x) {
            profit = x;
            psum += profit;//total profits
            psum2 += profit * profit;//sum of profit squared
            count++; //count of calls, ergo how many days have run
            average = psum / count; //avg profit
            variance = psum2 / count - average * average; //profit variance
            stdev = Math.sqrt(variance); //stdev of the profit
            return;
        }

        public double GetProfit() {
            return profit;
        }

        public double GetAverage() {
            return average;
        }

        public double GetVariance() {
            return variance;
        }

        public double GetStDev() {
            return stdev;
        }

        public int GetCount() {
            return count;
        }
    }//end CalcStats

    //DemandProcedure
    // -class that holds todays demand and the method to calculate it
    private static class DemandProcedure {

        private int demand;

        //constructor, start demand at zero
        public DemandProcedure() {
            demand = 0;
        }

        //gets todays demand
        //demand is calculated at random based on hard coded, weighted ratios for each possible demand qty
        public int DemandToday() {
            int x; //random variant
            x = (int) (Math.random() * 100); //random whole number between 0-100
            //treat whole num x as percentage range, evaluate based on occurrence values specified in write up
            // 1/12 chance = 8.3%chance, etc
            //ex: if x= 60, demand =18 because (58<x<75)
            if (x <= 8)//1/12 chance
                demand = 15;
            else if (x <= 16)//1/12 chance
                demand = 16;
            else if (x <= 58)//5/12 chance
                demand = 17;
            else if (x <= 75)//2/12 chance
                demand = 18;
            else if (x <= 92)//2/12 chance
                demand = 19;
            else            //1/12 chance
                demand = 20;
            return demand;
        }
    }//end DemandProcedure


}

