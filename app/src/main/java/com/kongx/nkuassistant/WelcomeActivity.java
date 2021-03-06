package com.kongx.nkuassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.kongx.javaclasses.CourseSelected;
import com.kongx.javaclasses.ExamCourse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.kongx.nkuassistant.Information.COURSE_PREFS_NAME;
import static com.kongx.nkuassistant.Information.EXAM_PREFS_NAME;
import static com.kongx.nkuassistant.Information.PREFS_NAME;
import static com.kongx.nkuassistant.Information.Strings;
import static com.kongx.nkuassistant.Information.curriculum_lastUpdate;
import static com.kongx.nkuassistant.Information.examCount;
import static com.kongx.nkuassistant.Information.exams;
import static com.kongx.nkuassistant.Information.facultyName;
import static com.kongx.nkuassistant.Information.id;
import static com.kongx.nkuassistant.Information.ids_major;
import static com.kongx.nkuassistant.Information.ids_minor;
import static com.kongx.nkuassistant.Information.ifRemPass;
import static com.kongx.nkuassistant.Information.isDoubleMajor;
import static com.kongx.nkuassistant.Information.lastVersion;
import static com.kongx.nkuassistant.Information.majorName;
import static com.kongx.nkuassistant.Information.minorName;
import static com.kongx.nkuassistant.Information.name;
import static com.kongx.nkuassistant.Information.newestNotice;
import static com.kongx.nkuassistant.Information.password;
import static com.kongx.nkuassistant.Information.scheduleTimeIsBusy;
import static com.kongx.nkuassistant.Information.selectedCourseCount;
import static com.kongx.nkuassistant.Information.selectedCourses;
import static com.kongx.nkuassistant.Information.version;
import static com.kongx.nkuassistant.Information.weekdays_tobalitai;
import static com.kongx.nkuassistant.Information.weekdays_tojinnan;
import static com.kongx.nkuassistant.Information.weekends_tobalitai;
import static com.kongx.nkuassistant.Information.weekends_tojinnan;


public class WelcomeActivity extends AppCompatActivity {

    private boolean componentReady = false;
    private Intent startIntent = null;
    final TimerTask timerTask = new TimerTask() {
        public void run() {
            synchronized (this) {
                while (!componentReady) try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            WelcomeActivity.this.startActivity(startIntent);
            WelcomeActivity.this.finish();
        }
    };

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        //Inflate view
        View v = View.inflate(getApplicationContext(), R.layout.activity_welcome, null);
        setContentView(v);
        new Timer().schedule(timerTask, 2000);

        //Play animation
        ImageView startImg1 = (ImageView) v.findViewById(R.id.startImg);
        ImageView startImg2 = (ImageView) v.findViewById(R.id.startImg2);
        startImg1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_fade_in));
        startImg2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_move_in));


        //Get configuration
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        ifRemPass = settings.getBoolean(Strings.setting_remember_pwd, false);

        //Get previous information
        lastVersion = settings.getString(Strings.setting_last_version,null);
        name = settings.getString(Strings.setting_student_name, null);
        facultyName = settings.getString(Strings.setting_student_faculty, null);
        id = settings.getString(Strings.setting_studentID, null);
        newestNotice = settings.getString(Strings.setting_notice, null) == null ? -1 : Integer.parseInt(settings.getString("newestNotice", null));
        password = settings.getString(Strings.setting_password,null);
        ids_major/*For new system*/ = settings.getString(Strings.setting_student_major_IDs, null);
        ids_minor/*For new system*/ = settings.getString(Strings.setting_student_minor_IDs, null);
        majorName = settings.getString(Strings.setting_student_major, null);
        minorName = settings.getString(Strings.setting_student_minor, null);
        isDoubleMajor = settings.getBoolean(Strings.setting_student_isDoubleMajor,false);
        Information.lastTimeStudiedCourseCount = settings.getInt(Strings.setting_studied_course_count, -1);
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) { Information.version = "unknown"; }
        if (!ifRemPass) startIntent = new Intent(this, EduLoginActivity.class);
        else if(lastVersion == null || !lastVersion.equals(version))    {startIntent = new Intent(this, EduLoginActivity.class);}
        else startIntent = new Intent(this, IndexActivity.class);
        lastVersion = version;
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Strings.setting_last_version,version);
        editor.apply();

        //get Curriculum Preferences
        scheduleTimeIsBusy = new boolean[14][7];
        settings = getSharedPreferences(COURSE_PREFS_NAME, 0);
        selectedCourseCount = settings.getInt(Strings.setting_selected_course_count, -1);
        curriculum_lastUpdate = settings.getString(Strings.setting_last_update_time, null);
        selectedCourses = new ArrayList<>();
        if (selectedCourseCount != -1) {
            CourseSelected tmpCourse;
            for (int i = 0; i < selectedCourseCount; i++) {
                tmpCourse = new CourseSelected();
                tmpCourse.setCourseSelectNum(settings.getString("courseSelectNum" + i, "null"));
                tmpCourse.setCourseName(settings.getString("name" + i, "null"));
                tmpCourse.setDayOfWeek(settings.getInt("dayOfWeek" + i, -1));
                tmpCourse.setStartTime(settings.getInt("startTime" + i, -1));
                tmpCourse.setEndTime(settings.getInt("endTime" + i, -1));
                tmpCourse.setClassRoom(settings.getString("classRoom" + i, "null"));
                tmpCourse.setTeacherName(settings.getString("teacherName" + i, "null"));
                tmpCourse.setStartWeek(settings.getInt("startWeek" + i, -1));
                tmpCourse.setEndWeek(settings.getInt("endWeek" + i, -1));
                tmpCourse.setColor(settings.getInt("color" + i, -1));
                selectedCourses.add(tmpCourse);
            }
            for(int i = 0;i < 14;i++){
                for(int j = 0;j < 7;j++){
                    Information.scheduleTimeIsBusy[i][j] = settings.getBoolean("isBusy"+i+j,false);
                }
            }
        }

        //get ExamCourse Preferences
        settings = getSharedPreferences(EXAM_PREFS_NAME, 0);
        examCount = settings.getInt(Strings.setting_exam_count, -1);
        if (examCount != -1) {
            ExamCourse tmpCourse;
            for (int i = 0; i < examCount; i++) {
                tmpCourse = new ExamCourse();
                tmpCourse.setCourseName(settings.getString("name" + i, "null"));
                tmpCourse.setTimePeriod(settings.getString("time" + i, "null"));
                tmpCourse.setClassRoom(settings.getString("classRoom" + i, "null"));
                tmpCourse.setSeatNum(settings.getString("seat" + i, "null"));
                tmpCourse.setDate(settings.getString("date" + i, "null"));
                exams.add(tmpCourse);
            }
        }

        readBusFile();
        Connector.getInformation(Connector.RequestType.STATISTIC,null,null);
        componentReady = true;
        synchronized (timerTask) {
            timerTask.notifyAll();
        }
    }
    private void readBusFile() {
        Element element = null;
        InputStream inputStream = null;
        try {
            Calendar calendar = Calendar.getInstance();
            inputStream = getAssets().open("timetable.xml");
        } catch (Exception e) {
            Log.e("WelcomeActivity", "Open bus file failed.");
        }
        DocumentBuilder documentBuilder = null;
        DocumentBuilderFactory documentBuilderFactory = null;
        weekdays_tojinnan = new ArrayList<>();
        weekdays_tobalitai = new ArrayList<>();
        weekends_tojinnan = new ArrayList<>();
        weekends_tobalitai = new ArrayList<>();
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            element = document.getDocumentElement();
            NodeList days = element.getChildNodes();
            NodeList weekdays_tojinnan_list = days.item(1).getChildNodes().item(1).getChildNodes();
            NodeList weekdays_tobalitai_list = days.item(1).getChildNodes().item(3).getChildNodes();
            NodeList weekends_tojinnan_list = days.item(3).getChildNodes().item(1).getChildNodes();
            NodeList weekends_tobalitai_list = days.item(3).getChildNodes().item(3).getChildNodes();
            HashMap<String, Integer> tmpMap;
            for (int i = 0; i < weekdays_tojinnan_list.getLength(); i++) {
                Node node = weekdays_tojinnan_list.item(i);
                if ("busItem".equals(node.getNodeName())) {
                    tmpMap = new HashMap<>();
                    tmpMap.put("id", Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()));
                    tmpMap.put("way", Integer.parseInt(node.getAttributes().getNamedItem("way").getNodeValue()));
                    tmpMap.put("hour", Integer.parseInt(node.getChildNodes().item(1).getTextContent()));
                    tmpMap.put("minute", Integer.parseInt(node.getChildNodes().item(3).getTextContent()));
                    weekdays_tojinnan.add(tmpMap);
                }
            }
            for (int i = 0; i < weekdays_tobalitai_list.getLength(); i++) {
                Node node = weekdays_tobalitai_list.item(i);
                if ("busItem".equals(node.getNodeName())) {
                    tmpMap = new HashMap<>();
                    tmpMap.put("id", Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()));
                    tmpMap.put("way", Integer.parseInt(node.getAttributes().getNamedItem("way").getNodeValue()));
                    tmpMap.put("hour", Integer.parseInt(node.getChildNodes().item(1).getTextContent()));
                    tmpMap.put("minute", Integer.parseInt(node.getChildNodes().item(3).getTextContent()));
                    weekdays_tobalitai.add(tmpMap);
                }
            }
            for (int i = 0; i < weekends_tojinnan_list.getLength(); i++) {
                Node node = weekends_tojinnan_list.item(i);
                if ("busItem".equals(node.getNodeName())) {
                    tmpMap = new HashMap<>();
                    tmpMap.put("id", Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()));
                    tmpMap.put("way", Integer.parseInt(node.getAttributes().getNamedItem("way").getNodeValue()));
                    tmpMap.put("hour", Integer.parseInt(node.getChildNodes().item(1).getTextContent()));
                    tmpMap.put("minute", Integer.parseInt(node.getChildNodes().item(3).getTextContent()));
                    weekends_tojinnan.add(tmpMap);
                }
            }
            for (int i = 0; i < weekends_tobalitai_list.getLength(); i++) {
                Node node = weekends_tobalitai_list.item(i);
                if ("busItem".equals(node.getNodeName())) {
                    tmpMap = new HashMap<>();
                    tmpMap.put("id", Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()));
                    tmpMap.put("way", Integer.parseInt(node.getAttributes().getNamedItem("way").getNodeValue()));
                    tmpMap.put("hour", Integer.parseInt(node.getChildNodes().item(1).getTextContent()));
                    tmpMap.put("minute", Integer.parseInt(node.getChildNodes().item(3).getTextContent()));
                    weekends_tobalitai.add(tmpMap);
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            Log.e("WelcomeActivity", e.toString());
        }
    }
}
