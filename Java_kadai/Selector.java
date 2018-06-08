package Java_kadai;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class Selector<T extends UserSelect> {
  T ask(BufferedReader input, String... preMessages) throws IOException {
    T userSelect = null;

    while (userSelect == null) {
      for (String message : preMessages) {
        System.out.println(message);
      }

      System.out.print(Stream.of(values()).map(String::valueOf).collect(Collectors.joining(" ", " ", " : ")));
      userSelect = from(input.readLine());

      if (userSelect == null) {
        System.out.println(Stream.of(Continuance.values()).map(r -> r.number)
            .collect(Collectors.joining(", ", "!!", "のいずれかの数字を入力してください。!!")));
        System.out.println("もう一度入力してください。");
      }
    }

    System.out.println("あなたの選択 : " + userSelect);
    return userSelect;
  }

  abstract UserSelect[] values();

  abstract T from(String string);
}

class ContinuanceSelector extends Selector<Continuance> {
  @Override
  UserSelect[] values() {
    return Continuance.values();
  }

  @Override
  Continuance from(String string) {
    return Continuance.from(string);
  }
}

class ForecastSelector extends Selector<Forecast> {
  @Override
  UserSelect[] values() {
    return Forecast.values();
  }

  @Override
  Forecast from(String string) {
    return Forecast.from(string);
  }
}
