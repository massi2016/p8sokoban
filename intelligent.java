package p8.demo.p8sokoban;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
// voila mon code avant modification



public class intelligent extends Activity {

    private IntilligentView sokoView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sokoView = (IntilligentView)findViewById(R.id.IntilligentView);
       sokoView.setVisibility(View.VISIBLE);
    }

    public void onResume(){
        super.onResume();
        sokoView.onResume();
    }
    public void onPause(){
        super.onPause();
        sokoView.onPause();

}

}
}







