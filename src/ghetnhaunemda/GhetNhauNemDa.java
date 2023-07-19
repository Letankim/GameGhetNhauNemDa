/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghetnhaunemda;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import javafx.scene.layout.Background;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 *
 * @author Le Tan Kim - CE170469
 */
public class GhetNhauNemDa extends javax.swing.JFrame {

    private static final int WIDTH_SCENCE = 805;
    private static final int HEIGHT_SCENCE = 600;
    private static final int ROCK_RADIUS = 30;
    // person
    private Person leftPerson;
    private Person rightPerson;
    //rock
    private JLabel rockLabel;
    private int xRock;
    private int yRock;

    private double strongThrow;
    private boolean isThrowDone = true;
    private int luotChoi; // luot choi bang 1 la ben trai -1 la ben phai
    private boolean isDoneGame = true;
//  direct and angle to thrown
    private int xDirectL;
    private int yDirectL;
    private int xDirectR;
    private int yDirectR;
    private boolean isChooseAngle;
    // to move rock
    private double initialX, initialY; // Initial position of the ball
    private double initialVelocity; // Initial velocity of the ball
    private double launchAngle; // Launch angle of the ball (in degrees)
    private double timeStep; // Time step for calculations
    private double currentTime; // Current time

    private double currentX, currentY; // Current position of the ball
    private double currentVelocityX, currentVelocityY; // Current velocities of the ball
    private long startTime = 0;
    private long endTime;
    private long timeStrong;
//  thread to count time
    Timer timer;
    Thread progressThread;
    Thread timeGame;
    private int timeInGame;
//  Text show when hit or hut
    private String[] textHut = {"Haha", "Hụt rồi", "Non"};
    private String[] textTrung = {"Hên thôi", "Hãy đợi đấy", "Không sao"};
    private String[] textTuBan = {"Xui thôi", "Ôi đen quá", "Toang rồi"};
    private boolean isTuBan = false;

//  graphic to draw
    Graphics2D g;

    /**
     * Creates new form GhetNhauNemDa
     */
    public void reset() {
        this.isTuBan = false;
        this.timeInGame = 0;
        this.labelMessage.setText("Left before");
        luotChoi = 1;
        leftPerson.setHp(100);
        rightPerson.setHp(100);
        this.labelLeftScore.setText("Left: " + leftPerson.getScore());
        this.labelRightScore.setText("Right: " + rightPerson.getScore());
        this.mainGame.setIcon(new ImageIcon(getClass().getResource("/image/bacground1.jpg")));
        this.labelLeftPerson.setIcon(new ImageIcon(getClass().getResource("/image/leftPlayer.png")));
        this.labelRightPerson.setIcon(new ImageIcon(getClass().getResource("/image/rightPlayer.png")));
        this.progressLeft.setSize(new Dimension(100, 14));
        this.progressRight.setSize(new Dimension(100, 14));
        this.showHp();
        this.runTimer();
    }

    public void resetDirect() {
        xDirectL = labelDirectLeft.getX() + labelDirectLeft.getWidth();
        yDirectL = labelDirectLeft.getY() + 25;
        xDirectR = labelDirectRight.getX();
        yDirectR = labelDirectRight.getY() + 25;
    }

    public void resetRock() {
        init();
        currentTime = 0;
        initialX = leftPerson.getX();
        initialY = leftPerson.getY();
        rockLabel.setIcon(null);
        timer.stop();
    }

    public void addRockLabel(int type) {
        int diameter = ROCK_RADIUS * 2;
        rockLabel = new JLabel();
        rockLabel.setPreferredSize(new Dimension(diameter, diameter));
        rockLabel.setIcon(new ImageIcon(getClass().getResource("/image/rook.png")));
        if (type == 1) {
            initialX = leftPerson.getX();
            initialY = leftPerson.getY();
        } else {
            initialX = rightPerson.getX();
            initialY = rightPerson.getY();
        }
        rockLabel.setLocation((int) initialX + 20, (int) initialY + 70);
        rockLabel.setBounds((int) initialX + 20, (int) initialY + 70, diameter, diameter);
        mainGame.add(rockLabel);
        repaint();
    }

    public void init() {
        mainGame.setLayout(null);
        mainGame.removeAll();
        mainGame.validate();
        mainGame.repaint();
        addRockLabel(1);
    }

    public void stopGame() {
        luotChoi = 0;
        timeGame.stop();
    }

    public String intToTime(int time) {
        return String.format("%02d:%02d:%02d", time / 3600, (time / 60) % 60, time % 60);
    }

    public void runTimer() {
        timeGame = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ++timeInGame;
                        labelTimer.setText(intToTime(timeInGame));
                        Thread.sleep(1000);
                    } catch (InterruptedException er) {
                        System.out.println(er);
                    }
                }
            }
        };
        timeGame.start();
    }

    public boolean isHit(int type, int xRock, int yRock) {
        if (type == -1) {
            if ((xRock + ROCK_RADIUS) > leftPerson.getX() && (xRock + ROCK_RADIUS) <= (leftPerson.getX() + labelLeftPerson.getWidth())
                    && (yRock + ROCK_RADIUS) > leftPerson.getY() && (yRock + ROCK_RADIUS) <= (leftPerson.getY() + labelLeftPerson.getHeight())) {
                this.labelLeftPerson.setIcon(new ImageIcon(getClass().getResource("/image/left_trung.png")));
                return true;
            }
        } else {
            if ((xRock + ROCK_RADIUS) > rightPerson.getX() && (xRock + ROCK_RADIUS) <= (rightPerson.getX() + labelRightPerson.getWidth())
                    && (yRock + ROCK_RADIUS) > leftPerson.getY() && (yRock + ROCK_RADIUS) <= (rightPerson.getY() + labelRightPerson.getHeight())) {
                this.labelRightPerson.setIcon(new ImageIcon(getClass().getResource("/image/right_trung.png")));
                return true;
            }
        }
        return false;
    }

    public boolean isOutScence(int xRock, int yRock) {
        if (yRock >= (leftPerson.getY() + labelLeftPerson.getHeight())
                || (xRock + rockLabel.getWidth()) <= 0 || (xRock - rockLabel.getWidth()) >= (labelRightPerson.getX() + labelRightPerson.getWidth())) {
            return true;
        }
        return false;
    }

    public void showHp() {
        progressHpLeft.setValue(leftPerson.getHp());
        progressHpRight.setValue(rightPerson.getHp());
    }

    public void moveRock(int type) {
        initialVelocity = 40.0 + strongThrow;
        timeStep = 0.1;

        currentX = initialX;
        currentY = initialY;
        currentVelocityX = type * initialVelocity * Math.cos(Math.toRadians(launchAngle));
        currentVelocityY = initialVelocity * Math.sin(Math.toRadians(launchAngle));

        timer = new Timer(25, e -> update(type)); // Update the ball position every 25 milliseconds
        timer.start();
    }

    public void statusOfPerson(int type) {
        if (type == -1) {
            this.leftPerson.setHp(this.leftPerson.getHp() - 20);
        } else if (type == 1) {
            this.rightPerson.setHp(this.rightPerson.getHp() - 20);
        }
        setStatusGame(type);
        this.showHp();
        this.resetRock();
        isThrowDone = true;
    }

    public void setStatusGame(int type) {
        if (type == -1) {
            if (leftPerson.getHp() == 0) {
                this.rightPerson.setScore(rightPerson.getScore() + 1);
                labelMessage.setText("Right win.");
                this.stopGame();
                this.btnStart.setText("Re-play");
                this.isDoneGame = true;
                resetRock();
                timeGame.stop();
            } else {
                if (!isTuBan) {
                    this.labelMessageLeft.setText(textTrung[randomText()]);
                }
                labelMessage.setText("The left's turn to play");
            }
        } else {
            if (rightPerson.getHp() == 0) {
                this.leftPerson.setScore(leftPerson.getScore() + 1);
                labelMessage.setText("Left win.");
                this.btnStart.setText("Re-play");
                this.stopGame();
                this.isDoneGame = true;
                resetRock();
                timeGame.stop();
            } else {
                if (!isTuBan) {
                    this.labelMessageRight.setText(textTrung[randomText()]);
                }
                labelMessage.setText("The right's turn to play");
            }
        }
    }

    public int randomText() {
        return (int) Math.floor(Math.random() * textHut.length);
    }

    public void setOutOfScence(int type) {
        if (launchAngle > 87) {
            if (type == 1) {
                this.leftPerson.setHp(this.leftPerson.getHp() - 20);
                this.labelLeftPerson.setIcon(new ImageIcon(getClass().getResource("/image/left_trung.png")));
                this.labelMessageLeft.setText(textTuBan[randomText()]);
            } else {
                this.rightPerson.setHp(this.rightPerson.getHp() - 20);
                this.labelRightPerson.setIcon(new ImageIcon(getClass().getResource("/image/right_trung.png")));
                this.labelMessageRight.setText(textTuBan[randomText()]);
            }
            this.isTuBan = true;
            this.setStatusGame(type * -1);
            this.showHp();
        } else if (type == -1) {
            labelMessage.setText("The left's turn to play");
            this.labelMessageLeft.setText(textHut[randomText()]);
            this.labelLeftPerson.setIcon(new ImageIcon(getClass().getResource("/image/left_hut.png")));
        } else {
            labelMessage.setText("The right's turn to play");
            this.labelMessageRight.setText(textHut[randomText()]);
            this.labelRightPerson.setIcon(new ImageIcon(getClass().getResource("/image/right_hut.png")));
        }
        this.resetRock();
        isThrowDone = true;
    }

    private void update(int type) {
        currentTime += timeStep;
        currentX = initialX + currentVelocityX * currentTime;
        currentY = initialY - (currentVelocityY * currentTime - 0.5 * 9.8 * currentTime * currentTime);
        boolean isHit = isHit(type, (int) currentX, (int) currentY);
        if (isHit) {
            this.statusOfPerson(type);
        } else if (isOutScence((int) currentX, (int) currentY)) {
            this.setOutOfScence(type);
        }
        int diameter = ROCK_RADIUS * 2;
        rockLabel.setBounds((int) currentX, (int) currentY, diameter, diameter);
    }

    public double calculateAngle(double[] vector1, double[] vector2) {
        double dotProduct = vector1[0] * vector2[0] + vector1[1] * vector2[1];
        double magnitude1 = Math.sqrt(vector1[0] * vector1[0] + vector1[1] * vector1[1]);
        double magnitude2 = Math.sqrt(vector2[0] * vector2[0] + vector2[1] * vector2[1]);

        double cosTheta = dotProduct / (magnitude1 * magnitude2);
        double angleRad = Math.acos(cosTheta);
        double angleDeg = Math.toDegrees(angleRad);

        return angleDeg;
    }

    public GhetNhauNemDa() {
        initComponents();
        resetDirect();
        this.setSize(new Dimension(WIDTH_SCENCE, HEIGHT_SCENCE));
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/image/logo.png")));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        leftPerson = new Person(100, labelLeftPerson.getX(), labelLeftPerson.getY() - labelLeftPerson.getHeight() - 20, 0);
        rightPerson = new Person(100, labelRightPerson.getX(), labelRightPerson.getY() - labelRightPerson.getHeight() - 20, 0);

    }

    public void hideMainGame() {
        this.labelLeftPerson.setIcon(null);
        this.labelRightPerson.setIcon(null);
        this.progressLeft.setSize(new Dimension(0, 0));
        this.progressRight.setSize(new Dimension(0, 0));
    }

    void drawLines(Graphics gh, int type) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        if (type == 1) {
            int xRootLeft = labelDirectLeft.getX();
            int yRootLeft = labelDirectLeft.getY() + labelDirectLeft.getHeight() + 25;
            Line2D lin = new Line2D.Float(xRootLeft, yRootLeft, xDirectL, yDirectL);
            g.draw(lin);
        } else {
            int xRootRight = labelDirectRight.getX() + labelDirectLeft.getWidth();
            int yRootRight = labelDirectRight.getY() + labelDirectRight.getHeight();
            Line2D lin = new Line2D.Float(xDirectR, yDirectR, xRootRight,
                    yRootRight + 25);
            g.draw(lin);
        }
    }

    public void paint(Graphics gh) {
        super.paint(gh);
        g = (Graphics2D) gh;
        drawLines(gh, 1);
        drawLines(gh, 0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelInfoGame = new javax.swing.JPanel();
        labelLeftScore = new javax.swing.JLabel();
        labelRightScore = new javax.swing.JLabel();
        labelMessage = new javax.swing.JLabel();
        progressHpLeft = new javax.swing.JProgressBar();
        progressHpRight = new javax.swing.JProgressBar();
        labelTimer = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnGuide = new javax.swing.JButton();
        labelIntroduction = new javax.swing.JButton();
        labelLeftPerson = new javax.swing.JLabel();
        labelRightPerson = new javax.swing.JLabel();
        progressLeft = new javax.swing.JProgressBar();
        progressRight = new javax.swing.JProgressBar();
        labelDirectLeft = new javax.swing.JLabel();
        labelDirectRight = new javax.swing.JLabel();
        labelMessageRight = new javax.swing.JLabel();
        labelMessageLeft = new javax.swing.JLabel();
        mainGame = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Stonning game");
        setMaximumSize(new java.awt.Dimension(800, 800));
        setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(null);

        panelInfoGame.setBorder(javax.swing.BorderFactory.createTitledBorder("Game infomation"));

        labelLeftScore.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        labelLeftScore.setForeground(new java.awt.Color(153, 0, 0));
        labelLeftScore.setText("Left:");

        labelRightScore.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        labelRightScore.setForeground(new java.awt.Color(51, 0, 102));
        labelRightScore.setText("Right: ");

        labelMessage.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelMessage.setForeground(new java.awt.Color(0, 0, 102));
        labelMessage.setText("StartGame");

        progressHpLeft.setBackground(new java.awt.Color(204, 204, 204));
        progressHpLeft.setForeground(new java.awt.Color(255, 0, 0));
        progressHpLeft.setToolTipText("");

        progressHpRight.setForeground(new java.awt.Color(255, 0, 0));

        labelTimer.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelTimer.setForeground(new java.awt.Color(0, 0, 153));
        labelTimer.setText("00:00:00");

        btnStart.setText("Start");
        btnStart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnStartMouseClicked(evt);
            }
        });
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnExit.setText("Exit");
        btnExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnExitMouseClicked(evt);
            }
        });

        btnGuide.setText("Guide");
        btnGuide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuideActionPerformed(evt);
            }
        });

        labelIntroduction.setText("Introduction");
        labelIntroduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelIntroductionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelInfoGameLayout = new javax.swing.GroupLayout(panelInfoGame);
        panelInfoGame.setLayout(panelInfoGameLayout);
        panelInfoGameLayout.setHorizontalGroup(
            panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInfoGameLayout.createSequentialGroup()
                .addGroup(panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(labelLeftScore))
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(progressHpLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(labelIntroduction)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuide))
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(labelTimer)))
                .addGroup(panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(progressHpRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(15, Short.MAX_VALUE))
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnStart)
                        .addGap(18, 18, 18)
                        .addComponent(btnExit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelRightScore)
                        .addGap(67, 67, 67))))
        );
        panelInfoGameLayout.setVerticalGroup(
            panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoGameLayout.createSequentialGroup()
                .addGroup(panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addGroup(panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnStart)
                            .addComponent(btnExit)
                            .addComponent(btnGuide)
                            .addComponent(labelIntroduction))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                        .addGroup(panelInfoGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelMessage)
                            .addComponent(labelTimer)))
                    .addGroup(panelInfoGameLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(labelRightScore, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(progressHpRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelInfoGameLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(labelLeftScore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressHpLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(panelInfoGame);
        panelInfoGame.setBounds(0, 0, 800, 100);

        labelLeftPerson.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/leftPlayer.png"))); // NOI18N
        labelLeftPerson.setPreferredSize(new java.awt.Dimension(120, 130));
        labelLeftPerson.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                labelLeftPersonMouseDragged(evt);
            }
        });
        labelLeftPerson.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelLeftPersonMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                labelLeftPersonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                labelLeftPersonMouseReleased(evt);
            }
        });
        getContentPane().add(labelLeftPerson);
        labelLeftPerson.setBounds(30, 410, 120, 120);

        labelRightPerson.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/rightPlayer.png"))); // NOI18N
        labelRightPerson.setPreferredSize(new java.awt.Dimension(120, 130));
        labelRightPerson.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                labelRightPersonMouseDragged(evt);
            }
        });
        labelRightPerson.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRightPersonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelRightPersonMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                labelRightPersonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                labelRightPersonMouseReleased(evt);
            }
        });
        getContentPane().add(labelRightPerson);
        labelRightPerson.setBounds(660, 410, 120, 120);
        getContentPane().add(progressLeft);
        progressLeft.setBounds(30, 390, 110, 14);

        progressRight.setToolTipText("");
        progressRight.setString("50%");
        getContentPane().add(progressRight);
        progressRight.setBounds(670, 390, 100, 14);

        labelDirectLeft.setBackground(new java.awt.Color(255, 255, 255));
        labelDirectLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelDirectLeftMouseClicked(evt);
            }
        });
        getContentPane().add(labelDirectLeft);
        labelDirectLeft.setBounds(120, 320, 100, 100);

        labelDirectRight.setBackground(new java.awt.Color(255, 255, 255));
        labelDirectRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelDirectRightMouseClicked(evt);
            }
        });
        getContentPane().add(labelDirectRight);
        labelDirectRight.setBounds(600, 320, 100, 100);

        labelMessageRight.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelMessageRight.setForeground(new java.awt.Color(255, 255, 0));
        getContentPane().add(labelMessageRight);
        labelMessageRight.setBounds(680, 360, 110, 20);

        labelMessageLeft.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelMessageLeft.setForeground(new java.awt.Color(255, 255, 0));
        getContentPane().add(labelMessageLeft);
        labelMessageLeft.setBounds(30, 360, 110, 16);

        mainGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bacground1.jpg"))); // NOI18N
        mainGame.setPreferredSize(new java.awt.Dimension(800, 400));
        getContentPane().add(mainGame);
        mainGame.setBounds(0, 100, 800, 460);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void labelLeftPersonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLeftPersonMouseEntered
        // TODO add your handling code here:
        if (luotChoi == 1) {
            this.labelRightPerson.setIcon(new ImageIcon(getClass().getResource("/image/rightPlayer.png")));
            this.labelLeftPerson.setIcon(new ImageIcon(getClass().getResource("/image/leftPlayer.png")));
            this.labelMessageRight.setText("");
            this.labelMessageLeft.setText("");
        }
    }//GEN-LAST:event_labelLeftPersonMouseEntered

    private void labelLeftPersonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLeftPersonMouseReleased
        // TODO add your handling code here:
        if (!isChooseAngle) {
            this.resetDirect();
            launchAngle = 45;
        }
        this.isTuBan = false;
        if (isThrowDone && luotChoi == 1) {
            timeStrong = (long) ((endTime - startTime) / 1000);
            if (timeStrong > 5) {
                timeStrong = 5;
            }
            strongThrow = timeStrong * 10;
            this.addRockLabel(1);
            this.moveRock(1);
            isThrowDone = false;
            this.resetThread(progressLeft);
            luotChoi = -1;
            isChooseAngle = false;
            this.resetDirect();
        }
    }//GEN-LAST:event_labelLeftPersonMouseReleased


    private void labelLeftPersonMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLeftPersonMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_labelLeftPersonMouseDragged

    private void labelRightPersonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRightPersonMouseEntered
        // TODO add your handling code here:
        if (luotChoi == -1) {
            this.labelRightPerson.setIcon(new ImageIcon(getClass().getResource("/image/rightPlayer.png")));
            this.labelLeftPerson.setIcon(new ImageIcon(getClass().getResource("/image/leftPlayer.png")));
            this.labelMessageRight.setText("");
            this.labelMessageLeft.setText("");
        }
    }//GEN-LAST:event_labelRightPersonMouseEntered
    public void runProgress(JProgressBar progress) {
        progressThread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        long proValue = (long) ((endTime - startTime) / 1000);
                        if (proValue >= 5) {
                            proValue = 5;
                        }
                        endTime += 1000;
                        progress.setValue((int) ((double) (proValue / (double) 5) * 100));
                        Thread.sleep(1000);
                    } catch (InterruptedException er) {
                        System.out.println(er);
                    }
                }
            }
        };
        progressThread.start();
    }

    private void labelRightPersonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRightPersonMouseReleased
        // TODO add your handling code here:
        if (!isChooseAngle) {
            launchAngle = 45;
        }
        this.isTuBan = false;
        if (isThrowDone && luotChoi == -1) {
            timeStrong = (long) ((endTime - startTime) / 1000);
            if (timeStrong > 5) {
                timeStrong = 5;
            }
            strongThrow = timeStrong * 10 - 2;
            this.addRockLabel(-1);
            this.moveRock(-1);
            this.resetThread(progressRight);
            isThrowDone = false;
            luotChoi = 1;
            isChooseAngle = false;
            this.resetDirect();
        }
    }//GEN-LAST:event_labelRightPersonMouseReleased

    public void resetThread(JProgressBar progress) {
        this.progressThread.stop();
        progress.setValue(0);
        endTime = 0;
    }
    private void labelRightPersonMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRightPersonMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRightPersonMouseDragged

    private void labelRightPersonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRightPersonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelRightPersonMouseClicked

    private void labelRightPersonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRightPersonMousePressed
        // TODO add your handling code here:
        if (isThrowDone && luotChoi == -1) {
            this.runProgress(progressRight);
        }
    }//GEN-LAST:event_labelRightPersonMousePressed

    private void labelLeftPersonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelLeftPersonMousePressed
        // TODO add your handling code here:
        if (isThrowDone && luotChoi == 1) {
            this.runProgress(progressLeft);
        }
    }//GEN-LAST:event_labelLeftPersonMousePressed

    private void btnStartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnStartMouseClicked
        // TODO add your handling code here:
        if (isDoneGame) {
            this.init();
            this.reset();
            isDoneGame = false;
        }
    }//GEN-LAST:event_btnStartMouseClicked

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnExitMouseClicked
        // TODO add your handling code here:
        String message = "";
        if (!isDoneGame) {
            message = "You are in the game. Are you sure you want to exit?";
        } else {
            message = "Do you want to exit?";
        }
        if (JOptionPane.showConfirmDialog(rootPane, message, "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(rootPane, "Goodbye and see you again");
            System.exit(0);
        }
    }//GEN-LAST:event_btnExitMouseClicked

    private void labelDirectLeftMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDirectLeftMouseClicked
        // TODO add your handling code here:
        if (isThrowDone && luotChoi == 1) {
            this.isTuBan = false;
            isChooseAngle = true;
            if (evt.getY() < 30) {
                xDirectL = labelDirectLeft.getX() + evt.getX();
            } else {
                xDirectL = labelDirectLeft.getX() + labelDirectLeft.getWidth();
            }
            yDirectL = evt.getY() + labelDirectLeft.getY() + 25;
            int x1 = labelDirectLeft.getX();
            int y1 = labelDirectLeft.getY() + labelDirectLeft.getHeight() + 25;
            int x2 = labelDirectLeft.getX() + labelDirectLeft.getWidth();
            int y2 = y1;
            double vector1[] = {x2 - x1, y2 - y1};
            int x3 = labelDirectLeft.getX() + evt.getX();
            int y3 = labelDirectLeft.getY() + 25 + evt.getY();
            double[] vector2 = {x3 - x1, y3 - y1};
            launchAngle = calculateAngle(vector1, vector2);
            repaint();
        }
    }//GEN-LAST:event_labelDirectLeftMouseClicked

    private void labelDirectRightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDirectRightMouseClicked
        // TODO add your handling code here:
        if (isThrowDone && luotChoi == -1) {
            this.isTuBan = false;
            isChooseAngle = true;
            if (evt.getY() < 30) {
                xDirectR = labelDirectRight.getX() + evt.getX();
            } else {
                xDirectR = labelDirectRight.getX();
            }
            yDirectR = evt.getY() + labelDirectRight.getY() + 25;
            int x1 = labelDirectRight.getX() + labelDirectRight.getWidth();
            int y1 = labelDirectRight.getY() + labelDirectRight.getHeight() + 25;
            int x2 = labelDirectRight.getX();
            int y2 = y1;
            double vector1[] = {x2 - x1, y2 - y1};
            int x3 = labelDirectRight.getX() + evt.getX();
            int y3 = labelDirectRight.getY() + 25 + evt.getY();
            double[] vector2 = {x3 - x1, y3 - y1};
            launchAngle = calculateAngle(vector1, vector2);
            repaint();
        }
    }//GEN-LAST:event_labelDirectRightMouseClicked

    private void btnGuideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuideActionPerformed
        // TODO add your handling code here:
        if (isDoneGame) {
            this.mainGame.setIcon(new ImageIcon(getClass().getResource("/image/guilde.jpg")));
            hideMainGame();
        }
    }//GEN-LAST:event_btnGuideActionPerformed

    private void labelIntroductionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelIntroductionActionPerformed
        // TODO add your handling code here:
        if (isDoneGame) {
            this.mainGame.setIcon(new ImageIcon(getClass().getResource("/image/introduction.jpg")));
            hideMainGame();
        }
    }//GEN-LAST:event_labelIntroductionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GhetNhauNemDa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GhetNhauNemDa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GhetNhauNemDa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GhetNhauNemDa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GhetNhauNemDa().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnGuide;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel labelDirectLeft;
    private javax.swing.JLabel labelDirectRight;
    private javax.swing.JButton labelIntroduction;
    private javax.swing.JLabel labelLeftPerson;
    private javax.swing.JLabel labelLeftScore;
    private javax.swing.JLabel labelMessage;
    private javax.swing.JLabel labelMessageLeft;
    private javax.swing.JLabel labelMessageRight;
    private javax.swing.JLabel labelRightPerson;
    private javax.swing.JLabel labelRightScore;
    private javax.swing.JLabel labelTimer;
    private javax.swing.JLabel mainGame;
    private javax.swing.JPanel panelInfoGame;
    private javax.swing.JProgressBar progressHpLeft;
    private javax.swing.JProgressBar progressHpRight;
    private javax.swing.JProgressBar progressLeft;
    private javax.swing.JProgressBar progressRight;
    // End of variables declaration//GEN-END:variables
}
