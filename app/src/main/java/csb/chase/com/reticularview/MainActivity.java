package csb.chase.com.reticularview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import csb.chase.com.reticularview.callback.MyOnSeekBarChangeListener;
import csb.chase.com.reticularview.view.PolygonView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PolygonView polygonView;
    private SeekBar sbNumber;
    private SeekBar sbAngle;
    private TextView tvBorderNumber;
    private TextView tvAngeValue;
    private int defaultNumber = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void setListener() {
        sbNumber.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer integer = Integer.valueOf(progress);
                defaultNumber = integer;
                setData(defaultNumber);
                polygonView.invalidate();
                tvBorderNumber.setText(""+progress);
            }
        });
        sbAngle.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                polygonView.setStartAngle(progress);
                polygonView.invalidate();
                tvAngeValue.setText(progress+"");
            }
        });
    }

    private void initView() {
        polygonView = findViewById(R.id.polygonView);
        sbNumber = findViewById(R.id.sbNumber);
        sbAngle = findViewById(R.id.sbAngle);
        tvBorderNumber = findViewById(R.id.tvBorderNumber);
        tvAngeValue = findViewById(R.id.tvAngleValue);
        setData(defaultNumber);
        sbNumber.setProgress(defaultNumber);
        tvBorderNumber.setText(""+defaultNumber);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sbAngle.setProgress((int) polygonView.getStartAngle());
        tvAngeValue.setText(polygonView.getStartAngle()+"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }
    private void setData(int number){
        List<String>list = new ArrayList<>();
        List<Double> listValue = new ArrayList<>();
        for (int i = 0; i <number;i++){
            int ch = 10;
            list.add(""+(i+ch));
            listValue.add(i*getRandom()*10);
        }
        polygonView.setValueData(listValue);
        polygonView.setData(list);
    }
    public double getRandom(){
        Random random = new Random(10);
        double aDouble = random.nextDouble();
        return aDouble;
    }
}
