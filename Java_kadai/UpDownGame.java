package Java_kadai;

/**
 * アップダウンゲーム
 * 
 * 1. プレイヤーは所持金として「10万G」持っています。
 * 
 * 2. 所持金を「50万G」まで増やすか、「0G」になったらゲーム終了です。
 * 
 * 3. ゲームが始まったら、プレイヤーがベットする金額を入力します。
 * 　 - 一度にベットできる金額は「3万G」までとします。
 * 　 - 所持金以上の金額をベットすることはできません。
 * 
 * 4. 1〜13までの数字がランダムに表示され、プレイヤーは次に表示される
 * 　 数字が現状の数字よりも大きい(UP)か小さいか(DOWN)か、同じか(SAME)かを選択します。
 * 
 * 5. UPもしくはDOWNで正解した場合にはベットした金額が二倍に、SAMEで正解した場合には
 * 　 5倍になります。プレイヤーは上がった金額でゲームを続けるか、再度ベットし直すかを
 * 　 選択します。不正解の場合には、ベットした金額は没収され、再びベットする金額を
 * 　 入力します。
 * 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Random;

public class UpDownGame {
  private final static int GAMEOVER_GOLD = 0; // ゲームオーバーになる金額
  private final static int GAMECLEAR_GOLD = 500_000; // ゲームクリアになる金額
  private final static int INITIAL_GOLD = 100_000; // 最初の所持金
  private final static int MAX_BET_GOLD = 30_000; // ベット額の上限

  private final int gameoverGold;
  private final int gameclearGold;
  private int pocket; // プレイヤーの所持金
  private final int maxBetGold;

  public UpDownGame() {
    gameoverGold = GAMEOVER_GOLD;
    gameclearGold = GAMECLEAR_GOLD;
    pocket = INITIAL_GOLD;
    maxBetGold = MAX_BET_GOLD;
  }

  public UpDownGame(int gameoverGold, int gameclearGold, int initialGold, int maxBetGold) {
    this.gameoverGold = gameoverGold;
    this.gameclearGold = gameclearGold;
    this.pocket = initialGold;
    this.maxBetGold = maxBetGold;
  }

  public static void main(String[] args) {
    new UpDownGame(GAMEOVER_GOLD, GAMECLEAR_GOLD, INITIAL_GOLD, MAX_BET_GOLD).play();
  }

  public void play() {
    System.out.println("ゲームスタート（所持金 : " + pocket + "G");

    try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
      while (!isFinish()) {
        int bet = decideBetGold(input); // ベット額を決める
        pocket -= bet; // 所持金からベット額を没収
        System.out.println("----------------------------");
        System.out.println("ベット額 : " + bet + "G");
        System.out.println("現在の所持金 : " + pocket + "G");
        System.out.println("----------------------------");

        pocket += deal(bet, input); // ゲームに勝った賞金がプラスされる(負けた場合は0)
        System.out.println("現在の所持金は" + pocket + "Gです。");
      }
    } catch (IOException e) {
      System.out.println(e);
      System.exit(1);
    }
  }

  /**
   * ゲーム終了判定
   * 
   * @return trueの場合ゲーム終了、falseの場合ゲーム継続
   */
  private boolean isFinish() {
    if (pocket >= gameclearGold) {
      System.out.println("所持金が" + gameclearGold + "Gに到達しました。");
      System.out.println("ゲームクリア");
      return true;
    }

    if (pocket <= gameoverGold) {
      System.out.println("所持金が" + gameoverGold + "G以下になりました。");
      System.out.println("ゲームオーバー");
      return true;
    }

    return false;
  }

  /**
   * ベット額を決めるメソッド
   * 
   * @param pocket
   *          : 現在の所持金
   * @param input
   *          : コンソール入力用BufferedReader
   * @return bet : ベット額
   * @throws IOException
   *           : 整数以外の値を入力した場合
   */
  private int decideBetGold(BufferedReader input) throws IOException {

    System.out.println("ベット額を入力してください。");
    System.out.println("(1度にベットできるのは" + maxBetGold + "Gまでです)");
    BetGold betgold = new BetGold(input.readLine(), maxBetGold, pocket);

    while (!betgold.checkValidity()) {
      System.out.println("もう一度ベット額を入力してください。");
      betgold = new BetGold(input.readLine(), maxBetGold, pocket);
    }

    return betgold.getBet();
  }

  private enum Forecast {
    DOWN("0") {
      @Override
      int checkAnswer(int bet, int result) {
        if (result < 0) {
          return bet * 2;
        }

        return 0;
      }

      @Override
      public String toString() {
        return "DOWN[0]";
      }
    },

    SAME("1") {
      @Override
      int checkAnswer(int bet, int result) {
        if (result == 0) {
          return bet * 5;
        }

        return 0;
      }

      @Override
      public String toString() {
        return "SAME[1]";
      }
    },

    UP("2") {
      @Override
      int checkAnswer(int bet, int result) {
        if (result > 0) {
          return bet * 2;
        }

        return 0;
      }

      @Override
      public String toString() {
        return "UP[2]";
      }
    };

    abstract int checkAnswer(int bet, int result);

    private final String number;

    private Forecast(final String number) {
      this.number = number;
    }

    static Forecast from(String string) {
      if (Forecast.DOWN.number.equals(string)) { // プレイヤーはDOWNを選択
        return Forecast.DOWN;
      }

      if (Forecast.SAME.number.equals(string)) {
        return Forecast.SAME;
      }

      return Forecast.UP;
    }

    private static boolean isValidAnswer(String answer) {
      if (Forecast.DOWN.number.equals(answer)) { // プレイヤーはDOWNを選択
        return true;
      }

      if (Forecast.SAME.number.equals(answer)) { // プレイヤーはSAMEを選択
        return true;
      }

      if (Forecast.UP.number.equals(answer)) { // プレイヤーはUPを選択
        return true;
      }

      return false;
    }
  }

  private int deal(int bet, BufferedReader input) throws IOException {
    Random random = new Random();
    int firstNumber = random.nextInt(13) + 1; // はじめの数字
    System.out.println("-> はじめの数字は" + firstNumber + "です");

    System.out.print("-> DOWN[0] SAME[1] UP[2] : ");
    String string = input.readLine();

    while (!Forecast.isValidAnswer(string)) {
      System.out.println("!! 0, 1, 2のいずれかの数字を入力してください。!!");
      System.out.println("もう一度入力してください。");
      System.out.print("-> DOWN[0] SAME[1] UP[2] : ");
      string = input.readLine();
    }

    Forecast answer = Forecast.from(string);
    System.out.println("あなたの選択 : " + answer);

    int secondNumber = random.nextInt(13) + 1;
    System.out.println("-> 2回目の数字は" + secondNumber + "でした"); // 2回目の数字
    int result = secondNumber - firstNumber;
    /*
     * result > 0 : 結果はUP result == 0 : 結果はSAME result < 0 : 結果はDOWN
     */
    int prize = answer.checkAnswer(bet, result);

    if (prize != 0) {
      System.out.println("-> " + prize + "Gの勝ち");
      /* 賞金を全額ベットして続行するか、賞金を獲得するか選択 */
      if (askContinue(prize, input) == true) {
        System.out.println("*********************");
        System.out.println("BET額" + prize + "Gで続行");
        prize = deal(prize, input);
      } else {
        System.out.println("*********************");
        System.out.println("賞金" + prize + "Gを獲得");
      }
    } else {
      System.out.println("-> まけ");
    }

    return prize;
  }

  /**
   * ゲームに勝った後に、獲得した賞金でそのままゲームを続けるか 獲得賞金を手に入れ、再度ベットをし直すかを決める 続ける場合
   * true、降りる場合はfalseを返す。
   * 
   * @param newBetG
   *          : 前回の獲得賞金(次のベット額)
   * @param input
   *          : コンソール入力用BufferedReader
   * @return trueの場合はゲーム続行、falseの場合は再度賞金獲得
   * @throws IOException
   *           : 整数以外の値を入力した場合
   */
  private boolean askContinue(int newBetG, BufferedReader input) throws IOException {
    System.out.println("このまま続けますか？");
    System.out.println("現在の賞金 : " + newBetG);
    System.out.println("*******************");
    System.out.print("いいえ[0] はい[1] : ");
    String reply = input.readLine();

    while (!isValidReply(reply)) {
      System.out.println("もう一度入力してください。");
      System.out.print("いいえ[0] はい[1] : ");
      reply = input.readLine();
    }

    if ("1".equals(reply)) {
      return true;
    }

    return false;
  }

  private boolean isValidReply(String reply) {
    if ("1".equals(reply) || "0".equals(reply)) {
      return true;
    }

    System.out.println("!! 0, 1 いずれかの数字を入力してください。!!");
    return false;
  }
}

class BetGold {
  final private int bet;
  private boolean isValid;

  public BetGold(String bet, int maxBetGold, int pocket) {
    int tmpBet = 0;
    try {
      tmpBet = Integer.parseInt(bet);
      if (maxBetGold < tmpBet) {
        System.out.println("!!" + maxBetGold + "G以下の金額を入力してください。!!");
        this.isValid = false;
      } else if (pocket < tmpBet) {
        System.out.println("!!ベット額が所持金を超えています。!!");
        this.isValid = false;
      } else if (tmpBet < 0) {
        System.out.println("!!ベット額がマイナスです。!!");
        this.isValid = false;
      } else {
        this.isValid = true;
      }
    } catch (NumberFormatException e) {
      System.out.println("!!整数以外が入力されました。!!");
      this.isValid = false;
    } finally {
      this.bet = tmpBet;
    }
  }

  public int getBet() {
    return bet;
  }

  public boolean checkValidity() {
    return isValid;
  }
}
