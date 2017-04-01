import java.util.ArrayList;
import java.util.Random;

class Chromosome {

    /**
     * The list of cities, which are the genes of this chromosome.
     */
    protected int[] cityList;

    /**
     * The cost of following the cityList order of this chromosome.
     */
    protected double cost;

    private Random random = new Random();

    /**
     *Constructor method for 
     * @param cities The order that this chromosome would visit the cities.
     * @param parent The best performing chromosome last time
     */
    Chromosome(City[] cities, int[] parent) {
        Random generator = new Random();
        cityList = new int[cities.length];

        //called from evolve
        if(parent.length>0){
            for (int x = 0; x < cities.length; x++) {
                cityList[x] = parent[x];
            }
            calculateCost(cities);
        }

        //called from main
        else {
            //cities are visited based on the order of an integer representation [o,n] of each of the n cities.
            for (int x = 0; x < cities.length; x++) {
                cityList[x] = x;
            }

            //shuffle the order so we have a random initial order
            for (int y = 0; y < cityList.length; y++) {
                int temp = cityList[y];
                int randomNum = generator.nextInt(cityList.length);
                cityList[y] = cityList[randomNum];
                cityList[randomNum] = temp;
            }

            calculateCost(cities);
            //nearest neighbour first
            int startingPoint = cityList[0];//the first city is always where we start, and we minimise distances from there
            int nextIndex = 1;
            int nextCity = cityList[1];
            int distance = cities[startingPoint].proximity(cities[nextCity]);
            //set it up so that the distance between the first two is minimised
            for(int i = 2; i<cityList.length; i++){
                int neighbourCity = cityList[i];
                int temp_dist = cities[startingPoint].proximity(cities[neighbourCity]);
                if(temp_dist<distance){
                    nextIndex = i;
                    distance = temp_dist;
                }
            }
            //if we found a closer city at a different index we swap them
            if(nextIndex!=1) {
                int temp = cityList[nextIndex];
                cityList[nextIndex] = cityList[1];
                cityList[1] = temp;
            }
            //now we order the rest according to the same principle
            for(int i = 1; i<cityList.length; i++){
                int currentCity = cityList[i];
                int neighbourIndex = i;
                int dist = 1000000000;
                for(int j = i+2; j<cityList.length; j++){
                    int alternativeNeighbour = cityList[j];
                    int alternativeDist = cities[currentCity].proximity(cities[alternativeNeighbour]);
                    if(alternativeDist<dist){
                        neighbourIndex = j;
                        dist = alternativeDist;
                    }
                }
                //update the order according to our results
                int temp = cityList[neighbourIndex];
                if(i!=cityList.length-1){
                    cityList[neighbourIndex] = cityList[i+1];
                    cityList[i+1] = temp;
                }
                else{
                    cityList[neighbourIndex] = cityList[i];
                    cityList[i] = temp;
                }
            }
            calculateCost(cities);
        }
    }

    /**
     * Calculate the cost of the specified list of cities.
     *
     * @param cities A list of cities.
     */
    /*GIVEN*/void calculateCost(City[] cities) {
        cost = 0;
        for (int i = 0; i < cityList.length - 1; i++) {
            double dist = cities[cityList[i]].proximity(cities[cityList[i + 1]]);
            cost += dist;
        }

        cost += cities[cityList[0]].proximity(cities[cityList[cityList.length - 1]]); //Adding return home
    }

    /**
     * Get the cost for this chromosome. This is the amount of distance that
     * must be traveled.
     */
    /*GIVEN*/double getCost() {
        return cost;
    }

    /**
     * @param i The city you want.
     * @return The ith city.
     */
    /*GIVEN*/int getCity(int i) {
        return cityList[i];
    }

    /**
     * Set the order of cities that this chromosome would visit.
     *
     * @param list A list of cities.
     */
    /*GIVEN*/void setCities(int[] list) {
        for (int i = 0; i < cityList.length; i++) {
            cityList[i] = list[i];
        }
    }

    /**
     * Set the index'th city in the city list.
     *
     * @param index The city index to change
     * @param value The city number to place into the index.
     */
    /*GIVEN*/void setCity(int index, int value) {
        cityList[index] = value;
    }

    /**
     *Generate upper and lower bound of sub array
     *@return bound[0] is the lower bound and bound[1] the upper bound
     */
    /*WRITTEN*/private int[] generate_bounds(){
        int[] bounds = new int[2];

        int lower_bound = random.nextInt(cityList.length);
        int upper_bound = random.nextInt(cityList.length);

        //lower = upper
        if (lower_bound == upper_bound){
            while(lower_bound == upper_bound ){
                upper_bound = random.nextInt(cityList.length);
            }
        }
        //lower bound < upper bound
        if (lower_bound>upper_bound){
            //upper bound has the lower bound value
            int temp = upper_bound;
            //upper bound has the upper bound value
            upper_bound = lower_bound;
            //lower bound has lower bound value
            lower_bound = temp;
        }

        bounds[0] = lower_bound;
        bounds[1] = upper_bound;
        return bounds;
    }

    /**
     * Reverses the order of values stored in an int array
     * @param array the array in need of reversing
     * @return the array with its indices reversed
     */
    /*WRITTEN*/private int[] reverse_array(int[] array){
        for(int i = 0; i<=array.length/2; i++){
            int temp = array[i];
            array[i] = array[array.length-i-1];
            array[array.length-i-1] = temp;
        }
        return array;
    }

    /**
     * Chromosome undergoes a mutation
     * @return the mutated chromosome
     */
    /*WRITTEN*/public int[] inversion(){
        //EDGE CASE: there is only one city or there are no cities
        if(cityList.length <= 1){
            return cityList;
        }

        int[] cityListMutated = new int[cityList.length];
        int[] bounds = generate_bounds();

        //create an array which is a mutated version of a subarray of cityList
        int len_subarray = (bounds[1]-bounds[0])+1;
        int[] subarray = new int[len_subarray];
        System.arraycopy(cityList,bounds[0],subarray,0,len_subarray);
        int[] mutated_subarray = reverse_array(subarray);

        //copy the values from cityList into cityListMutated up until the lower bound
        int limit = bounds[0]-1;
        //EDGE CASE: the lower bound is the first index
        if(bounds[0]>=0){
            limit = bounds[0];
        }
        System.arraycopy(cityList,0,cityListMutated,0,limit);

        //copy all the values from mutated_subarray into cityListMutated
        System.arraycopy(mutated_subarray, 0, cityListMutated, bounds[0], mutated_subarray.length);

        //copy all the values from cityList (occurring after the upper bound) into cityListMutated
        limit=bounds[1]+1;
        System.arraycopy(cityList,limit,cityListMutated,limit, cityListMutated.length-limit);

        return cityListMutated;
    }

    /**
     * Sort the chromosomes by their cost.
     *
     * @param chromosomes An array of chromosomes to sort.
     * @param num         How much of the chromosome list to sort.
     */
    /*GIVEN*/public static void sortChromosomes(Chromosome chromosomes[], int num) {
        Chromosome ctemp;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < num - 1; i++) {
                if (chromosomes[i].getCost() > chromosomes[i + 1].getCost()) {
                    ctemp = chromosomes[i];
                    chromosomes[i] = chromosomes[i + 1];
                    chromosomes[i + 1] = ctemp;
                    swapped = true;
                }
            }
        }
    }
}
