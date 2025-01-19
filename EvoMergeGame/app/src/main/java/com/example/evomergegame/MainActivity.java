package com.example.evomergegame;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Tile[][] tiles = new Tile[6][5]; // Игровое поле
    private GridLayout gridLayout;
    private int score = 200;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        initializeGame();
        startScoreTimer();
        Button spawnButton = findViewById(R.id.spawnButton);
        spawnButton.setOnClickListener(v -> spawnTile());
    }

    private void initializeGame() {
        gridLayout.setRowCount(6);
        gridLayout.setColumnCount(5);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        int padding = 16;
        gridLayout.setPadding(padding, padding, padding, padding);

        int margin = 4;
        int totalMargins = margin * (5 - 1);
        int availableWidth = screenWidth - (2 * padding) - totalMargins;

        int cellSize = availableWidth / 5;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tiles[i][j] = new Tile(0);

                ImageView imageView = new ImageView(this);
                imageView.setImageResource(getImageResource(tiles[i][j].getLevel()));

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.setMargins(margin, margin, margin, margin);
                imageView.setLayoutParams(params);

                imageView.setTag(new Position(i, j));
                imageView.setOnTouchListener(new TileTouchListener());
                imageView.setOnDragListener(new TileDragListener());

                gridLayout.addView(imageView);
            }
        }
        for (int j = 0; j < 5; j++) {
            tiles[5][j] = new Tile(-1);

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(getImageResource(tiles[5][j].getLevel()));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellSize;
            params.height = cellSize;
            params.rowSpec = GridLayout.spec(5);
            params.columnSpec = GridLayout.spec(j);
            params.setMargins(margin, margin, margin, margin);
            imageView.setLayoutParams(params);

            imageView.setTag(new Position(5, j));
            imageView.setOnTouchListener(new TileTouchListener());
            imageView.setOnDragListener(new TileDragListener());

            gridLayout.addView(imageView);
        }
    }

    private int getImageResource(float level) {
        if (level == 1.0f) return R.drawable.tile_level_1;
        if (level == 1.5f) return R.drawable.tile_level_1_5;
        if (level == 2.0f) return R.drawable.tile_level_2;
        if (level == 2.5f) return R.drawable.tile_level_2_5;
        if (level == 3.0f) return R.drawable.tile_level_3;
        if (level == 4.0f) return R.drawable.tile_level_4;
        if (level == 5.0f) return R.drawable.tile_level_5;
        if (level == 6.0f) return R.drawable.tile_level_6;
        if (level == 6.5f) return R.drawable.tile_level_6_5;
        if (level == 7.0f) return R.drawable.tile_level_7;
        if (level == 8.0f) return R.drawable.tile_level_8;
        if (level == -1.0f) return R.drawable.trash;

        return R.drawable.tile_empty;
    }

    private void spawnTile() {
        if (score < 10) {
            Toast.makeText(this, "Недостаточно очков для спавна!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<int[]> emptyTiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (tiles[i][j].getLevel() == 0) { // Если клетка пустая
                    emptyTiles.add(new int[]{i, j}); // Добавить координаты в список
                }
            }
        }

        if (emptyTiles.isEmpty()) {
            Toast.makeText(this, "Нет пустых клеток!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Выбираем случайную клетку из списка
        Random random = new Random();
        int[] randomTile = emptyTiles.get(random.nextInt(emptyTiles.size()));

        // Устанавливаем случайный уровень (1 или 1.5)
        float randomLevel = random.nextInt(100) < 70 ? 1 : 1.5f;
        tiles[randomTile[0]][randomTile[1]].setLevel(randomLevel);

        score -= 10;
        updateScoreDisplay();
        updateUI();
    }


    private void updateUI() {
        // Рассчитываем доступную ширину экрана
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Устанавливаем глобальные отступы (padding) для GridLayout
        int padding = 16; // Отступы по краям
        gridLayout.setPadding(padding, padding, padding, padding);

        // Отступы между ячейками
        int margin = 4;
        int totalMargins = margin * (5 - 1); // Отступы между ячейками в одной строке
        int availableWidth = screenWidth - (2 * padding) - totalMargins; // Оставшаяся ширина

        // Рассчитываем размер одной ячейки
        int cellSize = availableWidth / 5;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int index = i * 5 + j;
                ImageView imageView = (ImageView) gridLayout.getChildAt(index);

                // Устанавливаем размеры ячейки
                GridLayout.LayoutParams params = (GridLayout.LayoutParams) imageView.getLayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(margin, margin, margin, margin); // Отступы между ячейками
                imageView.setLayoutParams(params);

                // Обновляем изображение в ячейке
                imageView.setImageResource(getImageResource(tiles[i][j].getLevel()));
            }
        }
    }

    private void updateScoreDisplay() {
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("Очки: " + score);
    }


    private void startScoreTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateScore();
                handler.postDelayed(this, 1000); // Повторять каждую секунду
            }
        }, 1000);
    }

    private void updateScore() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Tile tile = tiles[i][j];
                if (tile.getLevel() == 1.5) score += 1;
                if (tile.getLevel() == 2.5) score += 3;
                if (tile.getLevel() == 3.5) score += 6;
            }
        }
        updateScoreDisplay();
    }

    private void mergeTiles(int x1, int y1, int x2, int y2) {
        float level1 = tiles[x1][y1].getLevel();
        float level2 = tiles[x2][y2].getLevel();
        if (x2 == 5 && y2 == 2) {
            tiles[x1][y1].setLevel(0);
            updateUI();
        }
        if (x1 == x2 && y1 == y2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Устанавливаем текст и картинку
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 50, 50, 50);
            TextView textView = new TextView(this);
            ImageView imageView = new ImageView(this);
            if (level1 == 1.0 ) {
                imageView.setImageResource(R.drawable.tile_level_1);
                builder.setTitle("Первичный бульён");
                textView.setText("Раствор богатый органическими соединениями, создающий идеальные условия для развития жизни.");
            } else if (level1 > 1.0 && level1 < 2.0) {
                imageView.setImageResource(R.drawable.tile_level_1_5);
                builder.setTitle("ДНК");
                textView.setText("Молекула с свойством саморепликации. Конфигурации ДНК формируют основу всех эволюционных изменений.");
            } else if (level1 == 2.0 ) {
                imageView.setImageResource(R.drawable.tile_level_2);
                builder.setTitle("Аминокислота");
                textView.setText("Атомы и молекулы соединяются вместе для создания исходных компонентов жизни.");
            } else if (level1 > 2.0 && level1 < 3.0) {
                imageView.setImageResource(R.drawable.tile_level_2_5);
                builder.setTitle("Ядро");
                textView.setText("Центр управления клетки. Содержит хромосомы в которых находится ДНК.");
            } else if (level1 == 3.0) {
                imageView.setImageResource(R.drawable.tile_level_3);
                builder.setTitle("Белок");
                textView.setText("Строительные блоки живых клеток.Это молекулы образованные из длинных цепей аминокислот.");
            } else if (level1 == 4.0) {
                imageView.setImageResource(R.drawable.tile_level_4);
                builder.setTitle("Прокариотическая клетка");
                textView.setText("Первый живой организм. У этих одноклеточных организмов нет ядра.");
            } else if (level1 == 5.0) {
                imageView.setImageResource(R.drawable.tile_level_5);
                builder.setTitle("Эукариотическая клетка");
                textView.setText("Более продвинутый собрат прокариотической клетки. Имеют ядро для хранения генетической информации.");
            } else if (level1 > 6.0 && level1 < 7.0) {
                imageView.setImageResource(R.drawable.tile_level_6_5);
                builder.setTitle("Губка");
                textView.setText("Первый многоклеточный организм. Эти неподвижные фильтраторы являются результатом объединения нескольких экуриотических клеток.");
            } else if (level1 == 6.0) {
                imageView.setImageResource(R.drawable.tile_level_6);
                builder.setTitle("Ткань");
                textView.setText("Группы клеток объединяются для выполнения конкретной функции.");
            } else if (level1 == 7.0) {
                imageView.setImageResource(R.drawable.tile_level_7);
                builder.setTitle("Мыщцы");
                textView.setText("Соединительная ткань в теле животного, способная сокращаться.");
            }else if (level1 == 8.0) {
                imageView.setImageResource(R.drawable.tile_level_8);
                builder.setTitle("Медуза");
                textView.setText("Мягкие, свобободно плавающие водные существа со студенистым зонтоподобным желудком и щупальцами. Сокращая тело, они могут двигаться в воде.");
            } else {
                imageView.setImageResource(R.drawable.tile_empty); // Ресурс по умолчанию
            }

            imageView.setAdjustViewBounds(true);
            imageView.setMaxHeight(200);



            textView.setPadding(0, 20, 0, 0);

            layout.addView(imageView);
            layout.addView(textView);

            builder.setView(layout);

            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.create().show();

            return;
        }
        if (level1 == -1){
            tiles[x2][y2].setLevel(0);
            updateUI();
            return;
        }
        if (level2 == -1){
            tiles[x1][y1].setLevel(0);
            updateUI();
            return;
        }
        if (level1 > 0 && level2 > 0) {
            float newLevel = level1 + 1;

            if ((level1 == 8 && level2 == 8) || (level1 == 2.5 && level2 == 2.5) || (level1 == 3 && level2 == 3) || (level1 == 4 && level2 == 4)){
                Toast.makeText(this, "Эти уровни не могут быть объединены!", Toast.LENGTH_SHORT).show();
                return;
            } else if ((level1 == 3 && level2 == 1.5) || (level1 == 1.5 && level2 == 3)) {
                newLevel = 4;
            }else if ((level1 == 4 && level2 == 2.5) || (level1 == 2.5 && level2 == 4)) {
                newLevel = 5;
            } else if ((level1 == 5 && level2 == 6) || (level1 == 6 && level2 == 5)) {
                newLevel = 6.5F;
            }
              else if (level1 == level2){
                newLevel = level1 + 1;
            } else {
                Toast.makeText(this, "Эти уровни не могут быть объединены!", Toast.LENGTH_SHORT).show();
                return;
            }



            tiles[x1][y1].setLevel(0);
            tiles[x2][y2].setLevel(newLevel);
            updateUI();
        } else {
            Toast.makeText(this, "Слияние невозможно!", Toast.LENGTH_SHORT).show();
        }
    }



    private class TileTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDragAndDrop(null, shadowBuilder, view, 0);
                return true;
            }
            return false;
        }
    }

    private class TileDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
                View draggedView = (View) dragEvent.getLocalState();
                Position startPosition = (Position) draggedView.getTag();
                Position endPosition = (Position) view.getTag();

                mergeTiles(startPosition.x, startPosition.y, endPosition.x, endPosition.y);
            }
            return true;
        }
    }

    private static class Position {
        int x, y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Tile {
        private float level;

        Tile(float level) {
            this.level = level;
        }

        float getLevel() {
            return level;
        }

        void setLevel(float level) {
            this.level = level;
        }
    }
}
