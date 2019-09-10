
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class LockTest {

  @Test
  public void lockUnlockTest() {
    final int THREADS_COUNT = 5;
    final int COUNT_NUMBERS = 4;
    ArrayList<Integer> listNumbers = new ArrayList<>();
    ArrayList<Integer> expectedList = new ArrayList<>();
    ArrayList<Thread> threads = new ArrayList<>();
    Lock locker = new Lock();

    for (int t = 1; t <= THREADS_COUNT; t++) {
      expectedList.addAll(
          Stream.iterate(1, x -> x <= COUNT_NUMBERS, x -> x+1)
              .collect(Collectors.toList()));

      Thread thread = new Thread(() -> {
        try {
          locker.lock(); // устанавливаем блокировку
          locker.lock();
          int x = 1;
          for (int i = 1; i <= COUNT_NUMBERS; i++) {
            listNumbers.add(x);
            System.out.printf("%s  - %d \n", Thread.currentThread().getName(), x);
            x++;
          }
          System.out.println();
        } catch (InterruptedException e) {
          System.out.println(e.getMessage());
        } finally {
          locker.unlock(); // снимаем блокировку
          locker.unlock();
        }
      });
      thread.setName("Thread " + t);
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    assertThat(expectedList, is(listNumbers));
  }
}