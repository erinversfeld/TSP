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

    Chromosome(City[] cities, int[] ordering) {
        random = new Random();

        cityList = new int[cities.length];
        for (int x = 0; x < cities.length; x++) {
            cityList[x] = ordering[x];
        }

        calculateCost(cities);
    }


    /**
     *
     * @param cities The order that this chromosome would visit the cities.
     * //@param order_given If the order is given the cities are not shuffled
     */
    Chromosome(City[] cities/*, boolean order_given*/) {
        Random generator = new Random();
        cityList = new int[cities.length];
        //cities are visited based on the order of an integer representation [o,n] of each of the n cities.
        for (int x = 0; x < cities.length; x++) {
            cityList[x] = x;
        }

       /* if(order_given){
            calculateCost(cities);
        }*/
        //shuffle the order so we have a random initial order
        /*else {*/
        for (int y = 0; y < cityList.length; y++) {
            int temp = cityList[y];
            int randomNum = generator.nextInt(cityList.length);
            cityList[y] = cityList[randomNum];
            cityList[randomNum] = temp;
        }

        calculateCost(cities);
        /*}*/
    }

    /**
     * Calculate the cost of the specified list of cities.
     *
     * @param cities A list of cities.
     */
    void calculateCost(City[] cities) {
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
    double getCost() {
        return cost;
    }

    /**
     * @param i The city you want.
     * @return The ith city.
     */
    int getCity(int i) {
        return cityList[i];
    }

    /**
     * Set the order of cities that this chromosome would visit.
     *
     * @param list A list of cities.
     */
    void setCities(int[] list) {
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
    void setCity(int index, int value) {
        cityList[index] = value;
    }


    /**
     *Generate upper and lower bound of sub array
     *@return bound[0] is the lower bound and bound[1] the upper bound
     */
    private int[] generate_bounds(){
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
    private int[] reverse_array(int[] array){
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
   /* public int[] mutate(){
        //EDGE CASE: there is only one city or there are no cities
        if(cityList.length <= 1){
            return cityList;
        }

        System.out.println("cityList: "+cityList.toString());
        int[] cityListMutated = new int[cityList.length];
        int[] bounds = generate_bounds();
        System.out.println("lower and upper bounds: "+bounds.toString());

        //create an array which is a mutated version of a subarray of cityList
        int len_subarray = (bounds[1]-bounds[0])+1;
        int[] subarray = new int[len_subarray];
        System.arraycopy(cityList,bounds[0],subarray,0,len_subarray);
        System.out.println("subarray: "+subarray.toString());
        int[] mutated_subarray = reverse_array(subarray);
        System.out.println("mutated_subarray: "+mutated_subarray.toString());

        //copy the values from cityList into cityListMutated up until the lower bound
        int limit = bounds[0]-1;
        System.arraycopy(cityList,0,cityListMutated,0,limit);
        System.out.println("cityListMutated, cityList below lower: "+cityListMutated.toString());

        //copy all the values from mutated_subarray into cityListMutated
        System.arraycopy(mutated_subarray, 0, cityListMutated, bounds[0], mutated_subarray.length);

        //copy all the values from cityList (occurring after the upper bound) into cityListMutated
        limit=bounds[1]+1;
        System.arraycopy(cityList,limit,cityListMutated,limit, cityListMutated.length-limit);
        System.out.println("cityListMutated, cityList above upper: "+cityListMutated.toString());

        return cityListMutated;
    }*/


    public int[] mutate() {
        // Add handling of singular city list
        if (cityList.length <= 1) {
            int[] copy = new int[cityList.length];
            System.arraycopy(cityList.clone(), 0, copy, 0, cityList.length);
            return copy;
        }

        // Find two different indices of the cityList
        int x1 = random.nextInt(cityList.length);
        int x2;
        do {
            x2 = random.nextInt(cityList.length);
        } while (x2 == x1);

        // Ensure that x1 < x2
        if (x1 > x2) {
            int tmp = x2;
            x2 = x1;
            x1 = tmp;
        }

        // Make a copy of the existing cityList and apply the inversion between x1 and x2
        int[] mutatedCityList = new int[cityList.length];
        System.arraycopy(cityList, 0, mutatedCityList, 0, cityList.length);
        int g = x1;
        for (int i = x2; i >= x1; --i, ++g) {
            mutatedCityList[g] = cityList[i];
        }

        return mutatedCityList;
    }

    /**
     * Sort the chromosomes by their cost.
     *
     * @param chromosomes An array of chromosomes to sort.
     * @param num         How much of the chromosome list to sort.
     */
    public static void sortChromosomes(Chromosome chromosomes[], int num) {
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
