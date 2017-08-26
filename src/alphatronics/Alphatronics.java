package alphatronics;

import gamecontroller.GameController;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
public class Alphatronics {
        
    private static String controllerName ;
        
    private static SerialPort serialPort ;
    
    private static int X_Value = 0;
    
    private static int Y_Value = 0;
    
    private static int Z_Value = 0;
    
    private static int R_Value = 0;
    
    private static String data;
    
    private static boolean simpleDirections = false;
    
    private static String comPort = "COM6" ;
    
    private static final String boost = "TURBO";
    
    private static final String normal = "NORMAL";
    
    private static final String slow = "LOW";
    
    private static final String stop = "STOP";
    
    private static int speed = 1; // 1 --> normal  0 --> slow  2 --> boost -1 --> stop
    
    private static boolean gasBeeb = true;
    
    private static boolean bodyBeeb = true;
    private static boolean tempBeeb = true;
    
    private static INIT_GUI gui = new INIT_GUI() {
        @Override
        public void onComPortSelected() {
            if(comPortsLST.getSelectedValue() != null){
                comPort = comPortsLST.getSelectedValue() ;
                serialPort = new SerialPort(comPort);
                try {
                    gui.setVisible(false);
                    mainGui.setVisible(true);
                    serialPort.openPort();//Open serial port
                    serialPort.setParams(SerialPort.BAUDRATE_9600, 
                                         SerialPort.DATABITS_8,
                                         SerialPort.STOPBITS_1,
                                         SerialPort.PARITY_NONE);
                }
                catch (SerialPortException ex) {
                    mainGui.connectedLBL.setText("Failed to Connect");
                    mainGui.reconnectBTN.setVisible(true);
                    Logger.getLogger(Alphatronics.class.getName()).log(Level.SEVERE, null, ex);
                    mainGui.gasValueLBL.setVisible(false);
                    mainGui.hiumidityLBL.setVisible(false);
                    mainGui.airTempLBL.setVisible(false);
                    mainGui.bodyTempLBL.setVisible(false);
                    mainGui.barometerLBL.setVisible(false);
                    readData.stop();
                }
                if(serialPort.isOpened()){
                    mainGui.reconnectBTN.setVisible(false);
                    mainGui.connectedLBL.setText("Connected");
                    readData.start();  
                    controller.startListening();
                }
            }else{
                JOptionPane.showMessageDialog(rootPane, "COM Port is not Selected", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void onControllerSelected() {
            controllerName = controllersLST.getSelectedValue();
            if(controllerName != null){
                controller.setController(controllerName);
                CardLayout card = (CardLayout)gui.jDesktopPane1.getLayout();
                card.show(jDesktopPane1, "comPorts");
                fillcomPortLST();
            }else{
                JOptionPane.showMessageDialog(rootPane, "Controller is not Selected", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void fillcomPortLST() {
            String[] portNames = SerialPortList.getPortNames();
            DefaultListModel model = new DefaultListModel();
            for(String portName : portNames ){
                model.addElement(portName);
            }
            comPortsLST.setModel(model);
        }

        @Override
        public void fillcontrollersLST() {
            DefaultListModel model = new DefaultListModel();
            for(String controller : GameController.getControllers()){
                model.addElement(controller);
            }
            controllersLST.setModel(model);
        }
    } ;
    
    private static MainGUI mainGui = new MainGUI() {
        @Override
        public void reconnectPressed() {
            mainGui.connectedLBL.setText("Connecting .....");
           try {
                if(serialPort.isOpened())serialPort.closePort();
                serialPort = new SerialPort(comPort);
                serialPort.openPort();
            } catch (SerialPortException ex) {
                mainGui.connectedLBL.setText("Failed to Connect");
                mainGui.reconnectBTN.setVisible(true);
                Logger.getLogger(Alphatronics.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(serialPort.isOpened()){
                mainGui.connectedLBL.setText("Connected");
                mainGui.reconnectBTN.setVisible(false);
                readData.start();  
                mainGui.gasValueLBL.setVisible(true);
                mainGui.hiumidityLBL.setVisible(true);
                mainGui.airTempLBL.setVisible(true);
                mainGui.bodyTempLBL.setVisible(true);
                mainGui.barometerLBL.setVisible(true);
            }
        }
    };
    
    private static final Timer readData = new Timer(200, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            WriteByBluetooth("e");
            HashMap<Byte,Integer> dataList = read();
            //mainGui.gasChart.addValue(dataList.get("G".getBytes()[0]));
            mainGui.gasValueLBL.setText(String.valueOf(dataList.get("G".getBytes()[0])));
            if(dataList.get("G".getBytes()[0])> 450 && gasBeeb){
                gasBeeb = false;
                mainGui.gasValueLBL.setForeground(Color.red);
                Toolkit.getDefaultToolkit().beep();
            }else{
                gasBeeb = true;
                mainGui.gasValueLBL.setForeground(Color.black);
            }
            if(dataList.get("T".getBytes()[0])>50 && tempBeeb){
                tempBeeb = false;
                Toolkit.getDefaultToolkit().beep();
                mainGui.airTempLBL.setForeground(Color.red);
            }else{
                tempBeeb = true;
                mainGui.airTempLBL.setForeground(Color.black);
            }
            if(dataList.get("C".getBytes()[0])>50 && bodyBeeb){
                bodyBeeb = false;
                Toolkit.getDefaultToolkit().beep();
                mainGui.bodyTempLBL.setForeground(Color.red);
            }else{
                bodyBeeb = true;
                mainGui.bodyTempLBL.setForeground(Color.black);
            }
            mainGui.gasChart.addValue(dataList.get("T".getBytes()[0]));
            mainGui.tempChart.addValue(dataList.get("T".getBytes()[0]));
            mainGui.airTempLBL.setText(String.valueOf(dataList.get("T".getBytes()[0])));
            mainGui.hiumidityChart.addValue(dataList.get("H".getBytes()[0]));
            mainGui.hiumidityLBL.setText(String.valueOf(dataList.get("H".getBytes()[0])));
            mainGui.carTempChart.addValue(dataList.get("C".getBytes()[0]));
            mainGui.bodyTempLBL.setText(String.valueOf(dataList.get("C".getBytes()[0])));
            mainGui.barometerChart.addValue((float)dataList.get("P".getBytes()[0])/1013f);
            mainGui.barometerLBL.setText(String.valueOf((float)dataList.get("P".getBytes()[0])/1013f));
            int theta = dataList.get("U".getBytes()[0]);
            int dist = dataList.get("O".getBytes()[0]);
            mainGui.radaGUI.move(theta);
            if(dist != 0 && dist <100) mainGui.radaGUI.addNewObject(theta, dist*5);
        }
    });
    
    private static GameController controller = new GameController() {
        @Override
        public void onButtonStateChange(Boolean state, String buttonName) {
            System.out.println(buttonName);
            switch(buttonName){
                case "4" :
                    System.out.println("SPEED UP" + state);
                    if(state){
                        System.out.println(data);
                        speed = 2;
                        readData.stop();
                        WriteByBluetooth("z");
                        WriteByBluetooth(data);
                        setSpeedToLBLs(speed);
                        readData.start();
                    }else{
                        System.out.println(data);
                        speed = 1;
                        readData.stop();
                        WriteByBluetooth("y");
                        WriteByBluetooth(data);
                        setSpeedToLBLs(speed);
                        readData.start();
                    }
                    break;
                    case "5" :
                    System.out.println("SPEED DOWN" + state);
                    if(state){
                        speed = 0;
                        readData.stop();
                        WriteByBluetooth("s");
                        WriteByBluetooth(data);
                        setSpeedToLBLs(speed);
                        readData.start();
                    }else{
                        speed = 1;
                        readData.stop();
                        WriteByBluetooth("y");
                        WriteByBluetooth(data);
                        setSpeedToLBLs(speed);
                        readData.start();
                    }
                    break;
                    case "6" :
                        if(state){
                            simpleDirections = true;
                            mainGui.motor1SpeedLBL.setText(mainGui.motor1SpeedLBL.getText()+" - Simple Directions");
                            mainGui.motor2SpeedLBL.setText(mainGui.motor2SpeedLBL.getText()+" - Simple Directions");
                            mainGui.motor3SpeedLBL.setText(mainGui.motor3SpeedLBL.getText()+" - Simple Directions");
                            mainGui.motor4SpeedLBL.setText(mainGui.motor4SpeedLBL.getText()+" - Simple Directions");
                        }else{
                            simpleDirections = false;
                            setSpeedToLBLs(speed);
                        }
            }
            
        }

        @Override
        public void onHatSwitchChange(float state) {
                        try{
                serialPort.writeBytes("v".getBytes());//Write data to port
                //serialPort.closePort();//Close serial port
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void on_X_AxisChange(int value) {
            X_Value = value;
            switch(checkFromTheOtherAxis("x")){
                case 0 :
                    switch(X_Value){
                        case 0 :
                            readData.stop();
                            WriteByBluetooth("n");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.normal);
                            data = "n";
                            mainGui.motor1SpeedLBL.setText("STOP");
                            mainGui.motor2SpeedLBL.setText("STOP");
                            mainGui.motor3SpeedLBL.setText("STOP");
                            mainGui.motor4SpeedLBL.setText("STOP");
                            readData.start();
                            break;
                        case 1 :
                            readData.stop();
                            WriteByBluetooth("r");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.right);
                            data = "r";
                            setSpeedToLBLs(speed);
                           readData.start();
                            break;
                        case -1 :
                            readData.stop();
                            WriteByBluetooth("l");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.left);
                            data = "l";
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                    }
                    break;
                case 1 :
                    switch(X_Value){
                        case 0 :
                            readData.stop();
                            WriteByBluetooth("b");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.down);
                            data = "b";
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case 1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("d"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.downRight);
                            data = getDirections("d");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case -1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("c"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.downLeft);
                            data = getDirections("c");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                    }
                    break;
                case -1 :
                    switch(X_Value){
                        case 0 :
                            readData.stop();
                            WriteByBluetooth("f");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.up);
                            data = "f";
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case 1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("x"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.upRight);
                            data = getDirections("x");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case -1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("a"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.upLeft);
                            data = getDirections("a");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                    }
                    break;
            }
        }

        @Override
        public void on_Y_AxisChange(int value) {
            Y_Value = value;
            switch(checkFromTheOtherAxis("y")){
                case 0 :
                    switch(Y_Value){
                        case 0 :
                            readData.stop();
                            WriteByBluetooth("n");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.normal);
                            data = "n";
                            mainGui.motor1SpeedLBL.setText(stop);
                            mainGui.motor2SpeedLBL.setText(stop);
                            mainGui.motor3SpeedLBL.setText(stop);
                            mainGui.motor4SpeedLBL.setText(stop);
                            readData.start();
                            break;
                        case 1 :
                            readData.stop();
                            WriteByBluetooth("b");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.down);
                            data = "b";
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case -1 :
                            readData.stop();
                            WriteByBluetooth("f");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.up);
                            data = "f";
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                    }
                    break;
                case 1 :
                    switch(Y_Value){
                        case 0 :
                            readData.stop();
                            WriteByBluetooth("r");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.right);
                            data = "r";
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case 1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("d"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.downRight);
                            data = getDirections("d");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case -1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("x"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.upRight);
                            data = getDirections("x");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                    }
                    break;
                case -1 :
                    switch(Y_Value){
                        case 0 :
                            readData.stop();
                            WriteByBluetooth("l");
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.left);
                            data = "l";
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case 1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("c"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.downLeft);
                            data = getDirections("c");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                        case -1 :
                            readData.stop();
                            WriteByBluetooth(getDirections("a"));
                            mainGui.gameControllerGUI1.change_XY_Analog(gameControllerGUI.AnalogValues.upLeft);
                            data = getDirections("a");
                            setSpeedToLBLs(speed);
                            readData.start();
                            break;
                    }
                    break;
            }
        }
        //      ********************************* LEFT ANALOG **************************************************
        @Override
        public void on_Z_AxisChange(int value) {
            Z_Value = value;
            switch(checkFromTheOtherAxis("z")){
                case 0 :
                    switch(Z_Value){
                        case 0 :
                            //WriteByBluetooth("n");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.normal);
                            data = "n2";
                            break;
                        case 1 :
                            //WriteByBluetooth("b");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.down);
                            data = "b";
                            break;
                        case -1 :
                            //WriteByBluetooth("f");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.up);
                            data = "f";
                            break;
                    }
                    break;
                case 1 :
                    switch(Z_Value){
                        case 0 :
                            //WriteByBluetooth("r");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.right);
                            data = "r";
                            break;
                        case 1 :
                            //WriteByBluetooth(getDirections("d"));
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.downRight);
                            data = getDirections("d");
                            break;
                        case -1 :
                            //WriteByBluetooth(getDirections("x"));
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.upRight);
                            data = getDirections("x");
                            break;
                    }
                    break;
                case -1 :
                    switch(Z_Value){
                        case 0 :
                            //WriteByBluetooth("l");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.left);
                            data = "l";
                            break;
                        case 1 :
                            //WriteByBluetooth(getDirections("c"));
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.downLeft);
                            data = getDirections("c");
                            break;
                        case -1 :
                            //WriteByBluetooth(getDirections("a"));
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.upLeft);
                            data = getDirections("a");
                            break;
                    }
                    break;
            }
        }

        @Override
        public void on_RZ_AxisChange(int value) {
            R_Value = value;
            switch(checkFromTheOtherAxis("r")){
                case 0 :
                    switch(R_Value){
                        case 0 :
                            //WriteByBluetooth("n");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.normal);
                            data = "n";
                            break;
                        case 1 :
                            //WriteByBluetooth("r");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.right);
                            data = "r";
                            break;
                        case -1 :
                            //WriteByBluetooth("l");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.left);
                            data = "l";
                            break;
                    }
                    break;
                case 1 :
                    switch(R_Value){
                        case 0 :
                            //WriteByBluetooth("b");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.down);
                            data = "b";
                            break;
                        case 1 :
                            
                            //WriteByBluetooth(getDirections("d"));
                            data = getDirections("d");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.downRight);
                            break;
                        case -1 :
                            //WriteByBluetooth(getDirections("c"));
                            data = getDirections("c");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.downLeft);
                            break;
                    }
                    break;
                case -1 :
                    switch(R_Value){
                        case 0 :
                            //WriteByBluetooth("f");
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.up);
                            data = "f";
                            break;
                        case 1 :
                            //WriteByBluetooth(getDirections("x"));
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.upRight);
                            data = getDirections("x");
                            break;
                        case -1 :
                            //WriteByBluetooth(getDirections("a"));
                            mainGui.gameControllerGUI1.change_ZR_Analog(gameControllerGUI.AnalogValues.upLeft);
                            data = getDirections("a");
                            break;
                    }
                    break;
            }
        }

        
    };
    
    private static int checkFromTheOtherAxis(String AxisName) {
        int temp = 0;
        switch(AxisName){
            case "x" :
                temp = Y_Value;
            break;
            case "y" :
                temp = X_Value;
            break;
            case "z" :
                temp = R_Value;
            break;
            case "r" :
                temp = Z_Value;
            break;
        }
        return temp;
    }
    
    private static void WriteByBluetooth(String data){
        System.out.println(data);
        try{
            serialPort.writeBytes(data.getBytes());
        }catch(Exception e){
        }
    }
    
    private static String getDirections(String parm) {
        String temp = parm;
        if(simpleDirections){
            if(parm.equals("a")){
                temp = "f";
            }else if(parm.equals("x")){
                temp = "f";
            }else if(parm.equals("c")){
                temp = "b";
            }else if(parm.equals("d")){
                temp = "b";
            }
        }
        return temp ;
    }
    
    private static HashMap<Byte,Integer> read(){
        HashMap<Byte , Integer> data = new HashMap<>();
        for (int b = 0 ; b<7 ; b++){
            try {
                byte key =serialPort.readBytes(1,500)[0];
                //System.out.println((char)key);
                byte number = serialPort.readBytes(1,500)[0];
                byte[] dataBytes = serialPort.readBytes(number,500);
                StringBuilder value = new StringBuilder();
                for(byte d : dataBytes){
                    value.append((char)d);
                }
                //System.out.println(value.toString());
                data.put(key, Integer.valueOf(value.toString()));
            } catch (SerialPortException ex) {
                ex.printStackTrace();
            } catch (SerialPortTimeoutException ex) {
                if(serialPort.isOpened()){
                    mainGui.connectedLBL.setText("Robot Disconnected");
                    mainGui.gasValueLBL.setVisible(false);
                    mainGui.hiumidityLBL.setVisible(false);
                    mainGui.airTempLBL.setVisible(false);
                    mainGui.bodyTempLBL.setVisible(false);
                    mainGui.barometerLBL.setVisible(false);
                    readData.stop();
                    mainGui.reconnectBTN.setVisible(true);
                    System.err.println("time out");
                }
            }
        }
        return data;
    }
    
    
        public static void main(String[] args) {
        
        
        gui.setVisible(true);
        CardLayout card = (CardLayout)gui.jDesktopPane1.getLayout();
        card.show(gui.jDesktopPane1, "controllers");
        
//        mainGui.setVisible(true);
//        mainGui.reconnectBTN.setVisible(false);
//        CardLayout card2 = (CardLayout)mainGui.jDesktopPane1.getLayout();
//        card2.show(mainGui.jDesktopPane1, "card2");
//        try {
//            serialPort = new SerialPort("COM6");
//            mainGui.statusLBL.setText("Connecting .....");
//            serialPort.openPort();
//        } catch (SerialPortException ex) {
//            mainGui.statusLBL.setText("Failed to Connect");
//            mainGui.reconnectBTN.setVisible(true);
//            Logger.getLogger(Alphatronics.class.getName()).log(Level.SEVERE, null, ex);
//            mainGui.gasValueLBL.setVisible(false);
//            mainGui.hiumidityLBL.setVisible(false);
//            mainGui.airTempLBL.setVisible(false);
//            mainGui.bodyTempLBL.setVisible(false);
//            mainGui.barometerLBL.setVisible(false);
//            readData.stop();
//        }
//        if(serialPort.isOpened()){
//            mainGui.statusLBL.setText("Connected");
//            readData.start();  
//        }
              
    } 
    
        private static void setSpeedToLBLs(int speed){
            if (speed == 0){
                mainGui.motor1SpeedLBL.setText(slow);
                mainGui.motor2SpeedLBL.setText(slow);
                mainGui.motor3SpeedLBL.setText(slow);
                mainGui.motor4SpeedLBL.setText(slow);
            } else if (speed == 1){
                mainGui.motor1SpeedLBL.setText(normal);
                mainGui.motor2SpeedLBL.setText(normal);
                mainGui.motor3SpeedLBL.setText(normal);
                mainGui.motor4SpeedLBL.setText(normal);
            }else if (speed == 2){
                mainGui.motor1SpeedLBL.setText(boost);
                mainGui.motor2SpeedLBL.setText(boost);
                mainGui.motor3SpeedLBL.setText(boost);
                mainGui.motor4SpeedLBL.setText(boost);
            }else{
                mainGui.motor1SpeedLBL.setText(stop);
                mainGui.motor2SpeedLBL.setText(stop);
                mainGui.motor3SpeedLBL.setText(stop);
                mainGui.motor4SpeedLBL.setText(stop);
            }
            
        }
}
