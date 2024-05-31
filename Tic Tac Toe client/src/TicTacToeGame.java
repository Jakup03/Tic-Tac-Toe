import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import javax.swing.*;
import java.util.Timer;
import static java.lang.System.exit;

public class TicTacToeGame extends JPanel implements ActionListener {
    static Font FONT = new Font("Helvetica", Font.BOLD, 75);
    Timer timer;
    int timerGraczX = 0;
    int timerGraczO = 0;
    JFrame frame = new JFrame();
    JPanel t_panel = new JPanel();
    JPanel bt_panel = new JPanel();
    JLabel textfield = new JLabel();
    JButton[] bton = new JButton[9];
    int chance_flag = 0;
    Random random = new Random();
    boolean pl1_chance;

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    TicTacToeGame() {
        hostConnection();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Nie udało się wysłać danych");
            System.out.println("Koniec programu");
            exit(3);
        }
        createMap();
        startGame();
    }

    public void hostConnection() {
        System.out.println("Łączenie z hostem");
        try {
            socket = new Socket("localhost", 1234);
        } catch (UnknownHostException e) {
            System.out.println("Nie znaleziono hosta");
            exit(1);
        } catch (IOException e) {
            System.out.println("Nie udało się połączyć z hostem");
            exit(2);
        }
        System.out.println("Połączono z hostem");
    }

    public void createMap() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 900);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setTitle("Tic Tac Toe");
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textfield.setBackground(Color.orange);
        textfield.setForeground(Color.WHITE);
        textfield.setFont(FONT);
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Tic Tac Toe");
        textfield.setOpaque(true);

        timer = new Timer();
        timer.scheduleAtFixedRate(new Task(), 0, 1000);
        timer.scheduleAtFixedRate(new readString(), 0, 100);

        t_panel.setLayout(new BorderLayout());
        t_panel.setBounds(0, 0, 800, 100);

        bt_panel.setLayout(new GridLayout(3, 3));
        bt_panel.setBackground(Color.darkGray);

        for (int i = 0; i < 9; i++) {
            bton[i] = new JButton();
            bt_panel.add(bton[i]);
            bton[i].setFont(FONT);
            bton[i].setText(" ");
            bton[i].setFocusable(false);
            bton[i].setBackground(Color.LIGHT_GRAY);
            bton[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            bton[i].setBackground(Color.LIGHT_GRAY);
            bton[i].addActionListener(this);
        }

        t_panel.add(textfield);
        frame.add(t_panel, BorderLayout.NORTH);
        frame.add(bt_panel);
    }

    public void startGame() {

        try {
            textfield.setText("Loading....");
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        pl1_chance = true;
        textfield.setText("O turn");
    }

    public void gameOver(String s){
        chance_flag = 0;
        Object[] option = {"Restart","Exit"};
        int n = JOptionPane.showOptionDialog(frame, "Game Over\n"+s,"Game Over",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,option,option[0]);
        frame.dispose();
        if ( n==0 ){
            new TicTacToeGame();
        }

    }

    public void matchCheck(String s) {
        if ((Objects.equals(bton[0].getText(), s)) && (Objects.equals(bton[1].getText(), s)) && (Objects.equals(bton[2].getText(), s))) {
            winInfo(0, 1, 2, s);
        }
        else if ((Objects.equals(bton[0].getText(), s)) && (Objects.equals(bton[4].getText(), s)) && (Objects.equals(bton[8].getText(), s))) {
            winInfo(0, 4, 8, s);
        }
        else if ((Objects.equals(bton[0].getText(), s)) && (Objects.equals(bton[3].getText(), s)) && (Objects.equals(bton[6].getText(), s))) {
            winInfo(0, 3, 6, s);
        }
        else if ((Objects.equals(bton[1].getText(), s)) && (Objects.equals(bton[4].getText(), s)) && (Objects.equals(bton[7].getText(), s))) {
            winInfo(1, 4, 7, s);
        }
        else if ((Objects.equals(bton[2].getText(), s)) && (Objects.equals(bton[4].getText(), s)) && (Objects.equals(bton[6].getText(), s))) {
            winInfo(2, 4, 6, s);
        }
        else if ((Objects.equals(bton[2].getText(), s)) && (Objects.equals(bton[5].getText(), s)) && (Objects.equals(bton[8].getText(), s))) {
            winInfo(2, 5, 8, s);
        }
        else if ((Objects.equals(bton[3].getText(), s)) && (Objects.equals(bton[4].getText(), s)) && (Objects.equals(bton[5].getText(), s))) {
            winInfo(3, 4, 5, s);
        }
        else if ((Objects.equals(bton[6].getText(), s)) && (Objects.equals(bton[7].getText(), s)) && (Objects.equals(bton[8].getText(), s))) {
            winInfo(6, 7, 8, s);
        }
        else if(chance_flag==9) {
            textfield.setText("Match Tie");
            gameOver("Match Tie");
        }
    }

    public void winInfo(int x1, int x2, int x3, String s) {
        sendString();
        bton[x1].setBackground(Color.GREEN);
        bton[x2].setBackground(Color.GREEN);
        bton[x3].setBackground(Color.GREEN);

        for (int i = 0; i < 9; i++) {
            bton[i].setEnabled(false);
        }
        textfield.setText(s+" wins");
        gameOver(s+" wins");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        for (int i = 0; i < 9; i++) {
            if (e.getSource() == bton[i]) {
                if (!pl1_chance && Objects.equals(bton[i].getText(), " ")) {
                    bton[i].setForeground(Color.RED);
                    bton[i].setText("O");
                    sendString();
                    pl1_chance = true;
                    chance_flag++;
                    matchCheck("X" );
                    matchCheck("O" );
                    if ( checkDraw() ) {
                        textfield.setText("Match Tie");
                        gameOver("Match Tie");
                    }
                }
            }
        }
    }
    private boolean checkDraw() {
        for ( int i = 0 ; i < 9 ; i++) {
            if (!Objects.equals(bton[i].getText(), "X") && !Objects.equals(bton[i].getText(), "O")) {
                return false;
            }
        }
        return true;
    }

    private void sendString() {
        out.println("Start");
        String s = "";
        for ( int i = 0 ; i < 9 ; i++) {
            s += bton[i].getText();
        }
        out.println(s);
    }


    private class readString extends TimerTask {
        @Override
        public void run() {
            try {
                if(!in.ready()) {
                    repaint();
                    return;
                }

                String wejscie = in.readLine();
                if (wejscie.equals("Start")) {
                    String s = in.readLine();
                    for (int i = 0 ; i < 9 ; i++) {
                        bton[i].setText(String.valueOf(s.charAt(i)));
                    }
                    matchCheck("X" );
                    matchCheck("O" );
                    if ( checkDraw() ) {
                        textfield.setText("Match Tie");
                        gameOver("Match Tie");
                    }
                    pl1_chance = false;
                }
            } catch (IOException e) {
                System.out.println("Nie udało się odczytać danych");
                System.out.println("Koniec programu");
                exit(4);
            }
            repaint();
        }
    }

    private class Task extends TimerTask {
        @Override
        public void run() {
            if ( chance_flag != 0 && chance_flag != 9) {
                if (!pl1_chance) {
                    timerGraczO++;
                    textfield.setText("O turn \n"+ toMinutes(timerGraczO));
                }
                else {
                    timerGraczX++;
                    textfield.setText("X turn \n"+ toMinutes(timerGraczX));
                }
                textfield.repaint();
            }
        }
    }
    String toMinutes(int timer) {
        int minuty = timer / 60;
        int sekundy = timer - minuty * 60;
        if (sekundy < 10) {
            return minuty + ":0" + sekundy;
        } else {
            return minuty + ":" + sekundy;
        }
    }
}