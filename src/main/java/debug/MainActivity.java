package debug;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yndongyong.widget.refreshlayout.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void btnCLick1(View view) {
        Intent intent = new Intent(this, LinearLayoutTestActivity.class);
        startActivity(intent);

    }

    public void btnCLick2(View view) {
        Intent intent = new Intent(this, GridLayoutTestActivity.class);
        startActivity(intent);

    }
}
