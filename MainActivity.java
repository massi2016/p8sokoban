package p8.demo.p8sokoban;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // Declaration des images
    private Bitmap 		bleu;
    private Bitmap 		rouge;
    private Bitmap 		vert;
    private Bitmap 		bleu1;
    private Bitmap 		rouge1;
    private Bitmap 		vert1;
    private Bitmap win;

    int score;// calcule le nombre de mouvement

    // timer
    Chronometer timer;
    CountDownTimer count;
    long time;
    Toast toast;

    int xx=0;
    int yy=0;
    double positionx,positiony;

    // Declaration des objets Ressources et Context permettant d'acc�der aux ressources de notre application et de les charger
    private Resources 	mRes;
    private Context 	mContext;

    // tableau modelisant la carte du jeu
    int[][] carte;
    int[][] carte1;



    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonn�es en Y du point d'ancrage de notre carte

    int        carteLeftAnchor;                  // coordonn�es en X du point d'ancrage de notre carte

    // placer la 2�me carte
    int        carteTopAnchor1;                   // coordonn�es en Y du point d'ancrage de notre carte
    int        carteLeftAnchor1;


    // taille de la carte
    static final int    carteWidth    = 5;
    static final int    carteHeight   = 5;
    static final int    carteTileSize = 50;


    // taille de la carte de reference
    static final int    carteWidth1   = 5;
    static final int    carteHeight1   = 5;
    static final int    carteTileSize1 = 16;

    // constante modelisant les differentes types de cases
    static final int    CST_bleu     = 0;
    static final int    CST_rouge   = 1;
    static final int    CST_vert     = 2;

    int [][] vect;


    // tableau de reference du terrain
    int [][] ref    = {
            {CST_rouge, CST_bleu, CST_rouge, CST_bleu,CST_bleu},
            {CST_bleu, CST_bleu, CST_bleu, CST_bleu,CST_bleu},
            {CST_vert, CST_vert, CST_rouge, CST_vert,CST_bleu},
            {CST_bleu, CST_bleu, CST_bleu, CST_bleu,CST_bleu},
            {CST_bleu, CST_rouge, CST_bleu, CST_bleu,CST_vert},
    };

    // tableau de reference du terrain
    int [][] ref1    = {
            {CST_vert, CST_bleu, CST_vert, CST_bleu,CST_bleu},
            {CST_bleu, CST_bleu, CST_bleu, CST_bleu,CST_bleu},
            {CST_vert, CST_rouge, CST_rouge, CST_vert,CST_bleu},
            {CST_bleu, CST_bleu, CST_bleu, CST_bleu,CST_bleu},
            {CST_bleu, CST_rouge, CST_bleu, CST_bleu,CST_rouge},
    };

    // position de reference des diamants
    int [][] refdiamants   = {
            {0, 3},
            {1, 3},
            {2, 3},
            {3, 3},
            {0, 2},
            {3, 1}
    };

    // position de reference du joueur
    int refxPlayer = 4;
    int refyPlayer = 1;

    // position courante des diamants
    int [][] diamants   = {
            {2, 3},
            {2, 6},
            {6, 3},
            {6, 6}
    };

    // position courante du joueur
    int xPlayer = 4;
    int yPlayer = 1;

    /* compteur et max pour animer les zones d'arriv�e des diamants */
    int currentStepZone = 0;
    int maxStepZone     = 4;

    // thread utiliser pour animer les zones de depot des diamants
    private     boolean in      = true;
    private     Thread  cv_thread;
    SurfaceHolder holder;

    Paint paint;

    /**
     * The constructor called from the main JetBoy activity
     *
     * @param context
     * @param attrs
     */
    public MainActivity(Context context, AttributeSet attrs) {
        super(context, attrs);


        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext	= context;
        mRes 		= mContext.getResources();
        bleu 		= BitmapFactory.decodeResource(mRes, R.drawable.bleu11);
        rouge		= BitmapFactory.decodeResource(mRes, R.drawable.rouge11);
        vert		= BitmapFactory.decodeResource(mRes, R.drawable.vert11);

        bleu1 		= BitmapFactory.decodeResource(mRes, R.drawable.blue5);
        rouge1    = BitmapFactory.decodeResource(mRes, R.drawable.rouge5);
        vert1		= BitmapFactory.decodeResource(mRes, R.drawable.vert5);
        win		= BitmapFactory.decodeResource(mRes, R.drawable.win);
        // initialisation des parmametres du jeu
        initparameters();

        // creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);


    }

    // chargement du niveau a partir du tableau de reference du niveau
    private void loadlevel() {
        // grande matrice
        for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                carte[j][i] = ref[j][i];

            }
        }

        // matrice resultat
        for (int i=0; i< carteHeight1; i++) {
            for (int j=0; j< carteWidth1; j++) {
                carte1[j][i]= ref1[j][i];

            }
        }
    }


    // initialisation du jeu
    public void initparameters() {
        paint = new Paint();
        paint.setColor(0xff0000);

        paint.setDither(true);
        paint.setColor(0xFFFFFF00);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
        paint.setTextAlign(Paint.Align.LEFT);
        carte           = new int[carteHeight][carteWidth];
        carte1           = new int[carteHeight1][carteWidth1];

        loadlevel();
        //loadlevel1();
        carteTopAnchor  = (getHeight()- carteHeight*carteTileSize);  //1
        carteLeftAnchor = (getWidth()- carteWidth*carteTileSize)/2;   //2
        //
        carteTopAnchor1  = (getHeight()- carteHeight*carteTileSize1);   //8
        carteLeftAnchor1 = (getWidth()- carteWidth*carteTileSize1)/2;    //2

        xPlayer = refxPlayer;
        yPlayer = refyPlayer;


        for (int i=0; i< 4; i++) {
            diamants[i][1] = refdiamants[i][1];
            diamants[i][0] = refdiamants[i][0];
        }
        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
        count = new CountDownTimer(250000, 1000) {

            public void onTick(long millisUntilDebuted ) {

                time = millisUntilDebuted / 1000;


                CharSequence text = String.valueOf("Attention au temps!");
                int duration = Toast.LENGTH_SHORT;
                toast = Toast.makeText(mContext, text, duration);
                if((time<=10) && (time> 5))  toast.show();
            }

            @Override
            public void onFinish() {
                toast.cancel();
                perte();
            }

        };
        count.start();
    }

    public void perte(){

        AlertDialog.Builder	alert = new AlertDialog.Builder(mContext);
        alert.setTitle("GAME OVER");
        alert.setIcon(R.drawable.ic_about);

        TextView l_viewabout	= new TextView(mContext);

        l_viewabout.setText("Votre score: " + score);
        l_viewabout.setGravity(FOCUS_UP);
        l_viewabout.setBackgroundColor(Color.LTGRAY);
        l_viewabout.setTextColor(Color.RED);
        l_viewabout.setTextSize(25);
        alert.setView(l_viewabout);
        alert.setPositiveButton("replay",new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toast.cancel();
                        loadlevel();
                        initparameters();


                    }
                }
        );
        alert.setNegativeButton("menu",new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        count.cancel();
                        Intent intent = new Intent(mContext, IntilligentView.class);

                        mContext.startActivity(intent);

                    }
                }
        );

        alert.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                                      @Override
                                      public void onCancel(DialogInterface dialog) {

                                      }
                                  }
        );
        alert.show();
    }



    // dessin du gagne si gagne
    private void paintwin(Canvas canvas) {
        canvas.drawBitmap(win, 150, 150, null);


    }
    // dessin du score
    private void paintscore(Canvas canvas) {
        Paint paintscore = new Paint();
        //paintscore.setStyle(Paint.Style.STROKE);

        paintscore.setColor(Color.RED);
        paintscore.setTextSize(15);
        String drawString = "SCORE : " + score;
        canvas.drawText(drawString, 25, 80, paintscore);
        canvas.drawText("TEMPS: 0" + time / 60 + ":" + time % 60, 220, 80, paintscore);
    }

    // dessin de la carte du jeu
    private void paintcarte(Canvas canvas,int[][] map, int type) {

        int margeTop = 50;
        int tileSize = carteTileSize1;
        if ( type == 0 ){ // dessiner la grande matrice
            margeTop = 170;
            tileSize = carteTileSize;
        }
        for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {

                int x = carteLeftAnchor + j*tileSize;
                int y =  margeTop + i*tileSize;

                int x1 = carteLeftAnchor1 + j*tileSize;
                int y1 =  margeTop + i*tileSize;
                Bitmap tile = null;
                Bitmap tile1 = null;
                switch (map[i][j]) {
                    case CST_bleu:
                        tile =bleu;
                        tile1 = bleu1;
                        break;
                    case CST_rouge:
                        tile = rouge;
                        tile1 = rouge1;
                        // canvas.drawBitmap(zone[currentStepZone],carteLeftAnchor+ j*carteTileSize,100+ position[j]+ i*carteTileSize, null);
                        break;
                    case CST_vert:
                        tile = vert;
                        tile1 = vert1;
                        // canvas.drawBitmap(vide,carteLeftAnchor+ j*carteTileSize , 100+position[j]+ i*carteTileSize, null);
                        break;
                }
                if ( type == 0 ){ // dessiner la grande matrice
                    canvas.drawBitmap(tile,x,y, null);
                } else{
                    canvas.drawBitmap(tile1,x1,y1, null);
                }

            }
        }
    }

    // permet d'identifier si la partie est gagnee (tous les diamants � leur place)
    private boolean isWon() {
        boolean win = true;// un bouleen qui permet de savoir si on a les mémes couleurs sur la carte et carte1
        int i=0,j=0;
        while(win & i<carteHeight) { // tant que on a pas atteind la derniere colonne et win est a vrai on parcour
            j = 0;
            while (win & j < carteHeight) { // tant que on a pas atteind la derniere ligne et win est a vrai on parcour
                Log.i("helllo","carte["+i+"]["+j+"]="+carte[i][j]+"    1==="+carte1[i][j]);
                if (carte[i][j] != carte1[i][j])
                {
                    win = false;
                }
                j++;
            }
            i++;

        }

        return win;
    }

    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(44, 44, 44);
        if (isWon()) {
            paintcarte(canvas, carte, 0);
            paintcarte(canvas, carte1, 1);
            Log.i("hey", "j'ai gagner");
            paintwin(canvas);
            paintscore(canvas);
        } else {
            paintcarte(canvas, carte, 0);
            paintcarte(canvas, carte1, 1);
            paintscore(canvas);

            Log.i("hey", "pas encore");
            // paintPlayer(canvas);
            // paintdiamants(canvas);

        }

    }

    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged "+ width +" - "+ height);
        initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
    }


    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
    }

    /**
     * run (run du thread cr��)
     * on endort le thread, on modifie le compteur d'animation, on prend la main pour dessiner et on dessine puis on lib�re le canvas
     */
    public void run() {
        Canvas c = null;
        while (in) {
            try {
                cv_thread.sleep(40);
                currentStepZone = (currentStepZone + 1) % maxStepZone;
                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN");
            }
        }
    }

    // verification que nous sommes dans le tableau
    private boolean IsOut(int x, int y) {
        if ((x < 0) || (x > carteWidth- 1)) {
            return true;
        }
        if ((y < 0) || (y > carteHeight- 1)) {
            return true;
        }
        return false;
    }

    // met � jour la position d'un diamant
    private void UpdateDiamant(int x, int y, int new_x, int new_y) {
        for (int i=0; i< 4; i++) {
            if ((diamants[i][1] == x) && (diamants[i][0] == y)) {
                diamants[i][1] = new_x;
                diamants[i][0] = new_y;
            }
        }
    }

    // fonction permettant de recuperer les evenements tactiles et faire bouger les blocks
    public boolean onTouchEvent (MotionEvent event) {
        Log.i("-> FCT <-", "onTouchEvent: "+ event.getX());// les méssages d'infomation pour débuguer le programme

        int x=0;// l'initialisation des varriables qui permettent de récuperer le touché
        int y=0;


        int action=event.getAction();
        positionx = event.getX();
        positiony=event.getY();

        x = (int) ((positionx-carteLeftAnchor)/carteTileSize);// l'equation qui nous permet de calculer x
        y= (int) ((positiony-170) / carteTileSize);//l'equation qui nous permet de calculer y
        Log.i("hello","x="+x+" ,y="+y);// les méssages d'infomation pour débuguer le programme
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                xx = x;// la valeur de x apres action down
                yy = y;// la valeur de y apres action down
                Log.i("hello", "down" + xx + " ," + yy);


                return true;

            case MotionEvent.ACTION_MOVE:
                Log.i("hello", "move" + xx + " ," + yy);

                if(x!=xx){// on compare la valeur de x quand le clic est effectuer avec sa veluer apres le mouvement

                    if(x-xx>0){// le mouvement est vers la droite
                        xx=x;


                        Log.i("hello", "move  right" + xx + " ," + yy);
                        int ligne[]=new int[5];
                        for(int i=0;i<carteHeight;i++){
                            ligne[i]=carte[yy][i];// on décale le vecteur horizontal de la matrice d'une position

                        }

                        for(int i=1;i<carteHeight;i++){
                            carte[yy][i]=ligne[i-1];// on décale le vecteur horizontal de la matrice d'une position vers la droite

                        }
                        carte[yy][0]=ligne[4];
                        score++;
                    }else{
                        xx=x;// on démarre de la position cliqué


                        Log.i("hello", "move   lefte" + xx + " ," + yy);
                        int ligne[]=new int[5];
                        for(int i=0;i<carteHeight;i++){
                            ligne[i]=carte[yy][i];
                        }

                        for(int i=0;i<(carteHeight-1);i++){
                            carte[yy][i]=ligne[(i+1)];// on décale le vecteur horizontal de la matrice d'une position vers la gauche

                        }
                        carte[yy][4]=ligne[0];

                        score++;
                    }
                }
                if(y!=yy){

                    if(y-yy>0){
                        Log.i("hello", "move  bas" + xx + " ," + yy);
                        yy=y;


                        Log.i("hello", "move  right" + xx + " ," + yy);
                        int ligne[]=new int[5];
                        for(int i=0;i<carteHeight;i++){
                            ligne[i]=carte[i][xx];
                        }

                        for(int i=1;i<carteHeight;i++){
                            carte[i][xx]=ligne[i-1];

                        }
                        carte[0][xx]=ligne[4];
                        score++;
                    }else{
                        Log.i("hello", "move   haut" + xx + " ," + yy);
                        yy=y;


                        Log.i("hello", "move   lefte" + xx + " ," + yy);
                        int ligne[]=new int[5];
                        for(int i=0;i<carteHeight;i++){
                            ligne[i]=carte[i][xx];
                        }

                        for(int i=0;i<(carteHeight-1);i++){
                            carte[i][xx]=ligne[(i+1)];

                        }
                        carte[4][xx]=ligne[0];
                        score++;
                    }
                }


                return true;

            case MotionEvent.ACTION_UP:
                return true;
        }

        return super.onTouchEvent(event);

    }

    public void onPause(){
        in=false;
        while(true){
            try{cv_thread.join();}
            catch(InterruptedException i){i.printStackTrace();}
            break;}
        cv_thread=null;
    }
    public void onResume(){
        in=true;
        cv_thread=new Thread(this);
        cv_thread.start();
    }

}

