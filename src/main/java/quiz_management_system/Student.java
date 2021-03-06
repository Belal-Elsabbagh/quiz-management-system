package quiz_management_system;

import quiz_management_system.Quiz.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;

/**
 * @author belsa
 */
public class Student extends User implements Interactive, Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    private ArrayList<Attempt> attemptHistory;

    public Student(String username, String password, Access student)
    {
        super(username, password, student);
        attemptHistory = new ArrayList<>();
    }

    /**
     * @deprecated
     */
    public static void consoleLogin()
    {
        Scanner sc = new Scanner(System.in);
        String inUsername, inPassword;
        System.out.println("*****Quiz Management System*****");
        System.out.println("Enter username:");
        inUsername = sc.next();
        System.out.println("Enter password:");
        inPassword = sc.next();
        Quiz_Management_System.setActiveUser(login(inUsername, inPassword));
        Quiz_Management_System.getActiveUser().listMenu();
    }

    public ArrayList<Attempt> getAttemptHistory()
    {
        return attemptHistory;
    }

    public Attempt getAttempt(String qID)
    {
        for (Attempt i : attemptHistory)
        {
            if (i.getQuiz().getQuizID().equals(qID))
                return i;
        }
        return null;
    }

    @Override
    public void listMenu()
    {
        JFrame window = new StudentWindow();
    }

    /**
     * @deprecated
     */
    @Override
    public int listMenuConsole()
    {
        out.println("*****Logged in as " + Quiz_Management_System.getActiveUser().getUsername() + "*****");
        out.println("1. Attempt quiz.");
        out.println("2. View attempt history.");
        out.println("3. Review Attempt.");
        out.println("Enter -1 to quit");
        Scanner sc = new Scanner(in);
        short n;
        n = sc.nextShort();
        switch (n)
        {
            case -1:
                return -1;
            case 1:
            {
                out.println("Enter Quiz ID: ");
                while (!sc.hasNextInt())
                {
                    err.println("INVALID INPUT.");
                    sc.next();
                }
                Quiz newQuiz = Quiz.searchByID(sc.next());
                if (newQuiz == null)
                {
                    err.println("No Quiz Found.");
                    return 0;
                }
                out.println("Starting Quiz: ");
                newQuiz.displayQuizPropertiesConsole();
                startAttempt(newQuiz);
            }
            case 2:
                viewAttemptHistoryConsole();
            case 3:
            {
                out.println("Enter Quiz ID: ");
                while (!sc.hasNextInt())
                {
                    err.println("INVALID INPUT.");
                    sc.next();
                }
                Quiz newQuiz = Quiz.searchByID(sc.next());
                if (newQuiz == null)
                {
                    err.println("No Quiz Found.");
                    return 0;
                }
                Attempt toReview = new Attempt(newQuiz);
                toReview.reviewAttemptConsole();
            }
        }
        return 1;
    }

    public void createNewAttempt(Quiz x)
    {
        new Attempt(x);
    }

    public Attempt searchAttemptHistoryByQuiz(Quiz x)
    {
        for (Attempt i : attemptHistory)
        {
            if (x.getQuizID() == i.getQuiz().getQuizID())
            {
                return i;
            }
        }
        return null;
    }

    public void startAttempt(Quiz newQuiz)
    {
        Attempt newAttempt = new Attempt(newQuiz);
    }

    /**
     * @deprecated not used with GUI.
     */
    public void viewAttemptHistoryConsole()
    {
        attemptHistory.forEach(i ->
        {
            double sum = 0;
            for (Question j : i.getModel())
            {
                sum += j.getGrade();
            }
            out.println("Quiz: " + i.quiz.getQuizTitle());
            out.println("Your result: " + i.result + "/" + sum);
        });
    }

    class StudentWindow extends JFrame implements ActionListener
    {
        // Title
        JPanel Title = new JPanel();
        Border brdr = BorderFactory.createLineBorder(new Color(222, 184, 150));
        Font myFont = new Font(Font.MONOSPACED, Font.BOLD, 30);
        JLabel Title_label = new JLabel("Welcome " + Quiz_Management_System.getActiveUser().getUsername()); // + username
        // Table
        JTable attemptTable;
        JPanel l = new JPanel();
        JLabel lb = new JLabel("Attempt History");
        // Buttons
        JButton actionAttempt = new JButton("Attempt Now");
        JButton actionReview = new JButton("Review quiz grades");
        JButton Back_button = new JButton("Log out");

        JButton Review_button = new JButton("Review quiz");
        //background
        JPanel Back = new JPanel();

        JPanel chat_panel = new JPanel();
        JButton actionCloseChat = new JButton("x");
        JButton actionOpenChat = new JButton(new ImageIcon("t1.jpeg"));

        JTextField qID = new JTextField(20);

        public StudentWindow()
        {
            JButton b = new JButton("send");
            b.setBounds(153, 480, 80, 30);
            b.setBackground(new Color(222, 184, 150));
            chat_panel.setBackground(new Color(239, 222, 205));
            JTextField t1 = new JTextField();
            chat_panel.setLayout(null);
            t1.setBounds(1, 480, 150, 30);
            chat_panel.setBounds(300, 1, 250, 550);
            JTextArea l1 = new JTextArea(100, 50);
            l1.setLineWrap(true);
            l1.setEditable(false);
            l1.setBounds(5, 35, 220, 440);
            actionCloseChat.setBounds(1, 1, 60, 30);
            actionCloseChat.setBackground(new Color(239, 222, 205));
            actionCloseChat.addActionListener(this);
            chat_panel.add(actionCloseChat);
            chat_panel.add(l1);
            chat_panel.add(t1);
            chat_panel.add(b);
            add(chat_panel);
            chat_panel.setVisible(false);
            actionOpenChat.setBounds(3, 1, 53, 53);
            actionOpenChat.addActionListener(this);
            add(actionOpenChat);

            Title.add(Title_label);
            add(Title, BorderLayout.PAGE_START);
            Title_label.setFont(myFont);
            Title_label.setForeground(Color.BLACK);
            Title.setBackground(Color.WHITE);
            Title.setBorder(brdr);

            lb.setBounds(5, 70, 100, 30);

            constructData();
            attemptTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            //attemptTable.setPreferredSize(new Dimension(250, 400));
            JScrollPane lScroll = new JScrollPane(attemptTable);
            lScroll.setPreferredSize(new Dimension(250, 80));
            l.add(lScroll);
            l.setBackground(new Color(239, 222, 205));
            l.setBounds(5, 100, 280, 420);
            add(l);
            add(lb);

            qID.setBounds(300, 100, 200, 30);
            qID.setBackground(new Color(222, 184, 150));
            add(qID);

            //buttons
            actionAttempt.setBounds(300, 150, 200, 30);
            actionAttempt.setBackground(new Color(222, 184, 150));
            actionAttempt.addActionListener(this);
            add(actionAttempt);

            Review_button.setBounds(300, 190, 200, 30);
            Review_button.setBackground(new Color(222, 184, 150));
            Review_button.addActionListener(this);
            add(Review_button);

            Back_button.setBounds(430, 470, 100, 30);
            Back_button.setBackground(new Color(222, 184, 150));
            Back_button.addActionListener(this);
            add(Back_button);

            Back.setBackground(Color.WHITE);
            add(Back, BorderLayout.CENTER);

            setTitle("Logged in as " + Quiz_Management_System.getActiveUser().getUsername());
            setSize(550, 550);
            setResizable(false);
            setLocationRelativeTo(null); // to not have it open at the corner
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private void constructData()
        {
            DefaultTableModel data = new DefaultTableModel();
            String[] headers = {"ID", "Quiz", "Result"};
            data.setColumnIdentifiers(headers);
            for (Attempt i : attemptHistory)
            {
                Object[] row = new Object[]{i.getQuiz().getQuizID(), i.getQuiz().getQuizTitle(), i.getResult()};
                data.addRow(row);
            }
            attemptTable = new JTable(data);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == actionReview)
            {
                Attempt toReview = getAttempt(qID.getText());
                if (toReview == null)
                {
                    JOptionPane.showMessageDialog(null, "Quiz not found.");
                    return;
                }

                //TODO Load attempt object from table and create the window from it
                toReview.openReview();
                setVisible(false);
            }
            if (e.getSource() == actionAttempt)
            {
                Quiz newA;
                try
                {
                    newA = Quiz.searchByID(qID.getText());
                    new Attempt(newA);
                } catch (NullPointerException a)
                {
                    JOptionPane.showMessageDialog(null, "Quiz not found", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            if (e.getSource() == actionOpenChat)
            {

                setVisible(false);
                JFrame window = new ClientHandler();
            }
            if (e.getSource() == actionCloseChat)
            {
                chat_panel.setVisible(false);
            }
            if (e.getSource() == Back_button)
            {
                int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to close?", "Close?", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                {
                    try
                    {
                        ClientHandler activeClient = new ClientHandler(new Socket("localhost", 1010));
                    } catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                    JFrame window = new LoginWindow();
                }
            }
            if (e.getSource() == Review_button)
            {
                try
                {
                    searchAttemptHistoryByQuiz(Quiz.searchByID(qID.getText())).openReview();
                } catch (NullPointerException a)
                {
                    JOptionPane.showMessageDialog(null, "Quiz not found", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
    }

    public class Attempt implements Serializable
    {
        private Quiz quiz;
        private Question[] model;
        private int[] answerIndexes;
        private double result;

        public Attempt(Quiz newQuiz)
        {
            quiz = newQuiz;
            model = newQuiz.generateQuizModel();
            answerIndexes = new int[quiz.getNQuestions()];
            result = 0;
            new DoAttemptWindow();
        }

        public void openReview()
        {
            new ReviewQuestionAccess();
        }

        public int[] getAnswerIndex()
        {
            return answerIndexes;
        }

        public void setAnswerIndex(int[] answerIndex)
        {
            this.answerIndexes = answerIndex;
        }

        public double getResult()
        {
            return result;
        }

        public void setResult(double result)
        {
            this.result = result;
        }

        public Quiz getQuiz()
        {
            return quiz;
        }

        public Question[] getModel()
        {
            return model;
        }

        public void setModel(Question[] model)
        {
            this.model = model;
        }

        public void calculateResult()
        {
            for (int i = 0; i < quiz.getNQuestions(); i++)
            {
                if (model[i].getMCQ().getAnswerKeyIndex() == answerIndexes[i])
                    result += model[i].getGrade();
            }
        }

        private void addThisAttemptToHistory()
        {
            attemptHistory.add(this);
            User.updateActiveUser();
            DataHandler.save();
        }

        /**
         * @deprecated not used with GUI
         */
        public void doAttemptConsole()
        {
            Scanner sc = new Scanner(in);
            Duration d = new Duration();
            d.run();
            out.println("Starting Quiz...Enter answer index after the prompt appears.");
            answerIndexes = new int[quiz.getNQuestions()];
            for (int i = 0; i < quiz.getNQuestions(); i++)
            {
                model[i].displayQuestionConsole();
                answerIndexes[i] = sc.nextInt();

                if (model[i].checkAnswer((short) answerIndexes[i]))
                    result += model[i].getGrade();
            }
        }

        /**
         * @deprecated
         */
        public void reviewAttemptConsole()
        {
            for (int i = 0; i < quiz.getNQuestions(); i++)
            {
                model[i].displayQuestionConsole();
                out.println("Your Answer: " + answerIndexes[i]);
                if (model[i].checkAnswer((short) answerIndexes[i]))
                    out.println("Correct");
            }
        }

        /**
         * @author marma
         */
        class DoAttemptWindow extends JFrame implements ActionListener, QuestionAccess
        {
            JPanel Back = new JPanel(),
                    Title = new JPanel(),
                    down = new JPanel();
            Font myFont = new Font(Font.MONOSPACED, Font.BOLD, 30);
            JButton right_b = new JButton(new ImageIcon("UR.PNG")),
                    left_b = new JButton(new ImageIcon("UL.PNG")),
                    submit = new JButton("Submit"),
                    Back_button = new JButton("Leave");
            Border brdr = BorderFactory.createLineBorder(new Color(222, 184, 150));
            JRadioButton c1_r = new JRadioButton("Choice 1"),   // get the text written in choice 1
                    c2_r = new JRadioButton("Choice 2"),   // get the text written in choice 2
                    c3_r = new JRadioButton("Choice 3"),   // get the text written in choice 3
                    c4_r = new JRadioButton("Choice 4");   // get the text written in choice 4
            ButtonGroup choices = new ButtonGroup();
            private int currentQuestionIndex = 0;
            JLabel Title_label = new JLabel(quiz.getQuizTitle()),
                    prompt_label = new JLabel(model[0].getPrompt()),
                    choice_label = new JLabel("choices: "),
                    c1 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[0]),
                    c2 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[1]),
                    c3 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[2]),
                    c4 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[3]),
                    currentQuestionLabel = new JLabel("Question: " + (currentQuestionIndex + 1) + "/" + model.length),
                    grade = new JLabel(" Grade: " + model[currentQuestionIndex].getGrade()),
                    timer_label = new JLabel("Timer");

            public DoAttemptWindow()
            {
                //button
                submit.setFocusable(false);
                submit.setBounds(430, 470, 100, 30);
                submit.setBorder(BorderFactory.createEtchedBorder());
                submit.setBackground(new Color(222, 184, 150));
                submit.addActionListener(this);
                add(submit);

                Back_button.setBounds(20, 470, 100, 30);
                Back_button.setBackground(new Color(222, 184, 150));
                Back_button.addActionListener(this);
                add(Back_button);

                //Title
                Title.add(Title_label);
                add(Title, BorderLayout.PAGE_START);
                Title_label.setFont(myFont);
                Title_label.setForeground(Color.BLACK);
                Title.setBackground(Color.WHITE);
                Title.setBorder(brdr);
                // right/left buttons
                right_b.setBackground(Color.WHITE);
                if (currentQuestionIndex == quiz.getNQuestions() - 1)
                {
                    right_b.setEnabled(false);
                }
                left_b.setBackground(Color.WHITE);
                left_b.setEnabled(false);
                right_b.addActionListener(this);
                left_b.addActionListener(this);
                down.add(left_b, BorderLayout.EAST);
                down.add(right_b, BorderLayout.PAGE_END);
                down.setBackground(Color.WHITE);
                add(down, BorderLayout.PAGE_END);
                //labels
                currentQuestionLabel.setBounds(5, 60, 200, 30);
                prompt_label.setBounds(5, 100, 500, 30);
                choice_label.setBounds(5, 140, 100, 30);
                c1.setBounds(40, 180, 300, 30);
                c2.setBounds(40, 220, 300, 30);
                c3.setBounds(40, 260, 300, 30);
                c4.setBounds(40, 300, 300, 30);
                grade.setBorder(brdr);
                grade.setBounds(250, 60, 80, 30);
                timer_label.setBorder(brdr);
                timer_label.setBounds(350, 60, 150, 30);
                add(currentQuestionLabel);
                add(prompt_label);
                add(choice_label);
                add(c1);
                add(c2);
                add(c3);
                add(c4);
                add(grade);
                add(timer_label);
                //radio buttons
                c1_r.setBackground(Color.WHITE);
                c2_r.setBackground(Color.WHITE);
                c3_r.setBackground(Color.WHITE);
                c4_r.setBackground(Color.WHITE);
                choices.add(c1_r);
                choices.add(c2_r);
                choices.add(c3_r);
                choices.add(c4_r);
                c1_r.setBounds(5, 180, 20, 30);
                c2_r.setBounds(5, 220, 20, 30);
                c3_r.setBounds(5, 260, 20, 30);
                c4_r.setBounds(5, 300, 20, 30);
                add(c1_r);
                add(c2_r);
                add(c3_r);
                add(c4_r);

                //background
                Back.setBackground(Color.WHITE);
                add(Back, BorderLayout.CENTER);

                setTitle(quiz.getQuizTitle());
                setSize(550, 550);
                setResizable(false);
                setLocationRelativeTo(null); // to not have it open at the corner
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setVisible(true);

                java.util.Timer timer = new java.util.Timer(String.valueOf(1000));

                timer.scheduleAtFixedRate(new TimerTask() {
                    long i = quiz.getDuration();

                    public void run() {


                        long hr, min, sec, totalSec;
                        min = TimeUnit.SECONDS.toMinutes(i);
                        sec = i - (min * 60);
                        hr = TimeUnit.MINUTES.toHours(min);
                        min = min - (hr * 60);
                        i--;
                        timer_label.setText("Time left: " + hr + ":" + min + ":" + sec);

                        if (i < 0) {
                            timer.cancel();
                            timer_label.setText("Time Over");
                        }
                    }
                }, 0, 1000);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (e.getSource() == right_b)
                {
                    saveChoice();
                    goRight();
                }
                if (e.getSource() == left_b)
                {
                    saveChoice();
                    goLeft();
                }
                if (e.getSource() == submit)
                {
                    calculateResult();
                    addThisAttemptToHistory();
                }
                if (e.getSource() == Back_button){
                    int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to close?", "Close?",  JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION)
                    {
                        setVisible(false);
                    }

                }
            }

            private void saveChoice()
            {
                if (c1_r.isSelected())
                    answerIndexes[currentQuestionIndex] = 1;
                else if (c2_r.isSelected())
                    answerIndexes[currentQuestionIndex] = 2;
                else if (c3_r.isSelected())
                    answerIndexes[currentQuestionIndex] = 3;
                else if (c4_r.isSelected())
                    answerIndexes[currentQuestionIndex] = 4;
            }

            public void buttonsSet()
            {
                if (currentQuestionIndex == quiz.getNQuestions() - 1)
                {
                    right_b.setEnabled(false);
                }
                else
                {
                    right_b.setEnabled(true);
                }
                if (currentQuestionIndex == 0)
                {
                    left_b.setEnabled(false);
                }
                else
                {
                    left_b.setEnabled(true);
                }
            }

            @Override
            public void goRight()
            {
                currentQuestionIndex++;
                buttonsSet();
                refresh();
            }

            @Override
            public void goLeft()
            {
                currentQuestionIndex--;
                buttonsSet();
                refresh();
            }

            @Override
            public void refresh()
            {
                currentQuestionLabel.setText("Question: " + (currentQuestionIndex + 1) + "/" + getQuiz().getNQuestions());
                prompt_label.setText(model[currentQuestionIndex].getPrompt());
                grade.setText(" Grade: " + (model[currentQuestionIndex].getGrade()));
                c1.setText(model[currentQuestionIndex].getMCQ().getChoices()[0]);
                c2.setText(model[currentQuestionIndex].getMCQ().getChoices()[1]);
                c3.setText(model[currentQuestionIndex].getMCQ().getChoices()[2]);
                c4.setText(model[currentQuestionIndex].getMCQ().getChoices()[3]);
            }
        }

        /**
         * @author marma
         */
        class ReviewQuestionAccess extends JFrame implements ActionListener, QuestionAccess
        {
            JPanel Back = new JPanel(),
                    Title = new JPanel(),
                    down = new JPanel();
            Font myFont = new Font(Font.MONOSPACED, Font.BOLD, 30);
            JButton right_b = new JButton(new ImageIcon("UR.PNG")),
                    left_b = new JButton(new ImageIcon("UL.PNG")),
                    submit = new JButton("Close"),
                    Back_button = new JButton("Back");
            Border brdr = BorderFactory.createLineBorder(new Color(222, 184, 150));
            JRadioButton c1_r = new JRadioButton("Choice 1."),
                    c2_r = new JRadioButton("Choice 1."),
                    c3_r = new JRadioButton("Choice 1."),
                    c4_r = new JRadioButton("Choice 1.");
            ButtonGroup choices = new ButtonGroup();
            private int currentQuestionIndex = 0;
            JLabel Title_label = new JLabel("Quiz Title"),
                    prompt_label = new JLabel("((((Question here))))"),
                    choice_label = new JLabel("Choices: "),
                    c1 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[0]),
                    c2 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[1]),
                    c3 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[2]),
                    c4 = new JLabel(model[currentQuestionIndex].getMCQ().getChoices()[3]),
                    currentQuestionLabel = new JLabel("Question: " + (currentQuestionIndex + 1) + "/" + model.length),
                    Q = new JLabel("Question number: " + (currentQuestionIndex + 1)),
                    grade = new JLabel(" Grade: " + model[currentQuestionIndex].getGrade()),
                    answer = new JLabel(" The right answer is " + 1);

            public ReviewQuestionAccess()
            {
                //button
                Back_button.setBounds(430, 470, 100, 30);
                Back_button.setBackground(new Color(222, 184, 150));
                Back_button.addActionListener(this);
                add(Back_button);
                //Title
                Title.add(Title_label);
                add(Title, BorderLayout.PAGE_START);
                Title_label.setFont(myFont);
                Title_label.setForeground(Color.BLACK);
                Title.setBackground(Color.WHITE);
                Title.setBorder(brdr);
                // right/left buttons
                right_b.setBackground(Color.WHITE);
                left_b.setBackground(Color.WHITE);
                down.add(left_b, BorderLayout.EAST);
                down.add(right_b, BorderLayout.PAGE_END);
                down.setBackground(Color.WHITE);
                add(down, BorderLayout.PAGE_END);
                //labels
                Q.setBounds(5, 60, 200, 30);
                prompt_label.setBounds(5, 100, 500, 30);
                choice_label.setBounds(5, 140, 100, 30);
                c1.setBounds(40, 180, 100, 30);
                c2.setBounds(40, 220, 100, 30);
                c3.setBounds(40, 260, 100, 30);
                c4.setBounds(40, 300, 100, 30);
                grade.setBorder(brdr);
                grade.setBounds(400, 60, 100, 30);
                answer.setBorder(brdr);
                answer.setBounds(0, 350, 535, 30);
                add(Q);
                add(prompt_label);
                add(choice_label);
                add(c1);
                add(c2);
                add(c3);
                add(c4);
                add(grade);
                add(answer);
                //radio buttons
                c1_r.setBackground(Color.WHITE);
                c1_r.setEnabled(false);
                c2_r.setBackground(Color.WHITE);
                c2_r.setEnabled(false);
                c3_r.setBackground(Color.WHITE);
                c3_r.setEnabled(false);
                c4_r.setBackground(Color.WHITE);
                c4_r.setEnabled(false);
                choices.add(c1_r);
                choices.add(c2_r);
                choices.add(c3_r);
                choices.add(c4_r);
                c1_r.setBounds(5, 180, 20, 30);
                c2_r.setBounds(5, 220, 20, 30);
                c3_r.setBounds(5, 260, 20, 30);
                c4_r.setBounds(5, 300, 20, 30);
                add(c1_r);
                add(c2_r);
                add(c3_r);
                add(c4_r);

                //background
                Back.setBackground(Color.WHITE);
                add(Back, BorderLayout.CENTER);

                setTitle("Review Quiz (" + quiz.getQuizTitle() + ") Grades");
                setSize(400, 500);
                setResizable(false);
                setLocationRelativeTo(null); // to not have it open at the corner
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setVisible(true);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (e.getSource() == right_b)
                {
                    goRight();
                }
                if (e.getSource() == left_b)
                {
                    goLeft();
                }
                //TODO Change the submit button in Reviewing
                if (e.getSource() == submit)
                {
                    addThisAttemptToHistory();
                }
                if(e.getSource() == Back_button){
                    setVisible(false);
                    JFrame window = new StudentWindow();
                }
            }

            @Override
            public void goRight()
            {
                currentQuestionIndex++;
                refresh();
            }

            @Override
            public void goLeft()
            {
                currentQuestionIndex--;
                refresh();
            }

            @Override
            public void refresh()
            {
                answer.setText(String.valueOf(model[currentQuestionIndex].getMCQ().getAnswerKeyIndex() + 1));
                currentQuestionLabel.setText("Question: " + (currentQuestionIndex + 1) + "/" + model.length);
                grade.setText(String.valueOf(model[currentQuestionIndex].getGrade()));
                c1.setText(model[currentQuestionIndex].getMCQ().getChoices()[0]);
                c2.setText(model[currentQuestionIndex].getMCQ().getChoices()[1]);
                c3.setText(model[currentQuestionIndex].getMCQ().getChoices()[2]);
                c4.setText(model[currentQuestionIndex].getMCQ().getChoices()[3]);
            }
        }
    }
}