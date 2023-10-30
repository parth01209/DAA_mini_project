import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MergeSortComparison {
    public static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        for (int i = 0; i < n1; i++) {
            leftArr[i] = arr[left + i];
        }

        for (int j = 0; j < n2; j++) {
            rightArr[j] = arr[mid + 1 + j];
        }

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (leftArr[i] <= rightArr[j]) {
                arr[k] = leftArr[i];
                i++;
            } else {
                arr[k] = rightArr[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = leftArr[i];
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = rightArr[j];
            j++;
            k++;
        }
    }

    public static void singleThreadedMergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            singleThreadedMergeSort(arr, left, mid);
            singleThreadedMergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    public static class MultithreadedMergeSortTask extends RecursiveTask<Void> {
        private final int[] arr;
        private final int left;
        private final int right;

        public MultithreadedMergeSortTask(int[] arr, int left, int right) {
            this.arr = arr;
            this.left = left;
            this.right = right;
        }

        @Override
        protected Void compute() {
            if (left < right) {
                int mid = (left + right) / 2;

                RecursiveTask<Void> leftTask = new MultithreadedMergeSortTask(arr, left, mid);
                RecursiveTask<Void> rightTask = new MultithreadedMergeSortTask(arr, mid + 1, right);

                leftTask.fork();
                rightTask.fork();

                leftTask.join();
                rightTask.join();

                merge(arr, left, mid, right);
            }
            return null;
        }
    }

    public static void multithreadedMergeSort(int[] arr) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new MultithreadedMergeSortTask(arr, 0, arr.length - 1));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the size of the array: ");
        int size = scanner.nextInt();
        int[] arr = new int[size];

        System.out.println("Enter " + size + " integers:");
        for (int i = 0; i < size; i++) {
            arr[i] = scanner.nextInt();
        }

        int[] arrCopy = Arrays.copyOf(arr, arr.length);

        System.out.println("Original Array: " + Arrays.toString(arr));

        long startTime = System.nanoTime();
        singleThreadedMergeSort(arr, 0, arr.length - 1);
        long endTime = System.nanoTime();
        long singleThreadedTime = endTime - startTime;
        System.out.println("Single-Threaded Sorted Array: " + Arrays.toString(arr));
        System.out.println("Single-Threaded Time: " + singleThreadedTime + " ns");

        startTime = System.nanoTime();
        multithreadedMergeSort(arrCopy);
        endTime = System.nanoTime();
        long multithreadedTime = endTime - startTime;
        System.out.println("Multithreaded Sorted Array: " + Arrays.toString(arrCopy));
        System.out.println("Multithreaded Time: " + multithreadedTime + " ns");

        System.out.println("Multithreaded is " + (singleThreadedTime > multithreadedTime ? "faster" : "slower") + " than Single-Threaded.");
    }
}
