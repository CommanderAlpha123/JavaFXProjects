import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Game extends Application {

	//all global scope variables since we use multiple methods and classes. 
	static GameMenu gameMenu;
	static int speed = 1;
	static int width = 32;
	static int height = 24;
	static int foodX = 0;
	static int foodY = 0;
	static int score = -1;
	static int cornersize = 25;
	static List<Corner> snake = new ArrayList<>();
	static Dir direction = Dir.left;
	static boolean gameOver = false;
	static boolean hard = false;
	static Random rand = new Random();
	static AnimationTimer timer;
	static Stage window;
	static Scene square, over;
	static VBox iface;

	static VBox root1 = new VBox();
	static Scene game = new Scene(root1, width * cornersize, height * cornersize);
	static Canvas c = new Canvas(width * cornersize, height * cornersize);
	static GraphicsContext gc = c.getGraphicsContext2D();

	//https://beginnersbook.com/2014/09/java-enum-examples/
	public enum Dir {

		left, right, up, down, not

	}

	public static class Corner {

		int x;
		int y;

		public Corner(int x, int y) {

			this.x = x;
			this.y = y;
		}
	}

	@Override

	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;

		primaryStage.setTitle("Hungry Serpent");

		InputStream image = Files.newInputStream(Paths.get("Res/ICON.png"));
		Image img0 = new Image(image);
		image.close();
		
		primaryStage.getIcons().add(img0);
		primaryStage.show();
		
		String path = "src/Concerto.mp3";
		Media media = new Media(new File(path).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setStartTime(new Duration(0));
		mediaPlayer.play();
		mediaPlayer.setVolume(1);

		//Display title
		Text Title = new Text("H U N G R Y\nS E R P E N T");
		Title.setFill(Color.WHITE);
		Title.setTranslateX(100);
		Title.setTranslateY(110);
		Title.setFont(new Font("Verdana", 35));
		Title.setVisible(false);

		Pane root = new Pane();
		root.setPrefSize(800, 600);

		// background image - made using a logo maker
		InputStream is = Files.newInputStream(Paths.get("Res/Apple.png"));
		Image img = new Image(is);
		is.close();

		// sets size of image ^^
		ImageView imgView = new ImageView(img);
		imgView.setFitWidth(800);
		imgView.setFitHeight(600);

		gameMenu = new GameMenu();
		gameMenu.setVisible(false);
		
		Label PRESS = new Label("Please press SPACE to continue\n");
		PRESS.setTextFill(Color.WHITE);
		PRESS.setTranslateX(300);
		PRESS.setTranslateY(500);
		PRESS.setFont(new Font("Verdana", 15));

		// Variable for infinity
		double infinity = Double.POSITIVE_INFINITY;

		FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), PRESS);

		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.setCycleCount((int) infinity);
		fadeIn.setAutoReverse(true);
		fadeIn.play();

		root.getChildren().addAll(imgView, gameMenu, PRESS, Title); // Music

		// press space to continue
		
		Scene scene = new Scene(root);
		scene.setOnKeyPressed(event -> {

			if (event.getCode() == KeyCode.SPACE) {
				if (!gameMenu.isVisible()) {

					FadeTransition ft = new FadeTransition(Duration.seconds(1), gameMenu);
					ft.setFromValue(0);
					ft.setToValue(1);

					FadeTransition fi = new FadeTransition(Duration.seconds(1.5), Title);
					fi.setFromValue(0);
					fi.setToValue(1);

					gameMenu.setVisible(true);

					PRESS.setVisible(false);
					Title.setVisible(true);

					ft.play();
					fi.play();

				} else {

					gameMenu.setVisible(true);
					PRESS.setVisible(false);

				}
			}
		});

		Label finito = new Label("GAME OVER");
		finito.setFont(new Font("Impact", 100));
		finito.setTextFill(Color.BLACK);
		finito.setTranslateX(175);
		finito.setTranslateY(100);

		VBox done = new VBox();
		over = new Scene(done, 800, 600);

		MenuButton again = new MenuButton("             Play Again");
		again.setTranslateX(280);
		again.setTranslateY(150);
		again.setOnMouseClicked(e -> {

			gameOver = false;

			score = 0;

			if (hard == false) {
				speed = 1;
			} else if (hard == true) {
				speed = 2;
			}

			direction = Dir.left;

			snake.removeAll(snake);

			snake.add(new Corner(width / 2, height / 2));
			snake.add(new Corner(width / 2, height / 2));
			snake.add(new Corner(width / 2, height / 2));

			window.setScene(game);
			timer.start();

		});

		MenuButton backMenu = new MenuButton("           Back to Menu");
		backMenu.setTranslateX(280);
		backMenu.setTranslateY(170);
		backMenu.setOnMouseClicked(e -> {

			snake.clear();
			speed = 1;
			gameOver = false;
			window.setScene(scene);

		});

		done.getChildren().addAll(finito, again, backMenu);

		root1.getChildren().add(c);
		primaryStage.setScene(scene);
		primaryStage.show();

		gameOver = false;

	}

	public class GameMenu extends Parent {

		public GameMenu() {

			VBox menu0 = new VBox(11);
			VBox menu1 = new VBox(11);

			menu0.setTranslateX(100);
			menu0.setTranslateY(250);

			menu1.setTranslateX(100);
			menu1.setTranslateY(250);

			final int offset = 400;

			menu1.setTranslateX(offset);

			MenuButton btnEasy = new MenuButton(" EASY");
			btnEasy.setOnMouseClicked(event -> {

				gameOver = false;
				hard = false;
				score = -1;
				speed = 1;

				newFoodEasy();

				timer = new AnimationTimer() {

					long lastTick = 0;

					public void handle(long now) {

						if (lastTick == 0) {
							lastTick = now;
							tick(gc);
							return;

						}

						if ((now - lastTick) > 100000000 / speed) {
							lastTick = now;
							tick(gc);

						}
					}
				};

				timer.start();

				game.addEventFilter(KeyEvent.KEY_PRESSED, key -> {

					if (key.getCode() == KeyCode.UP && direction != Dir.down) {
						direction = Dir.up;
					}
					if (key.getCode() == KeyCode.LEFT && direction != Dir.right) {
						direction = Dir.left;
					}
					if (key.getCode() == KeyCode.DOWN && direction != Dir.up) {
						direction = Dir.down;
					}
					if (key.getCode() == KeyCode.RIGHT && direction != Dir.left) {
						direction = Dir.right;

					}

				});

				snake.add(new Corner(width / 2, height / 2));
				snake.add(new Corner(width / 2, height / 2));
				snake.add(new Corner(width / 2, height / 2));
				window.setScene(game);

			});

			MenuButton btnHard = new MenuButton(" HARD");

			btnHard.setOnMouseClicked(event -> {

				gameOver = false;
				hard = true;
				score=0;
				speed = 1;

				newFoodHard();

				timer = new AnimationTimer() {
					long lastTick = 0;

					public void handle(long now) {

						if (lastTick == 0) {
							lastTick = now;
							tick(gc);
							return;

						}

						if ((now - lastTick) > 100000000 / speed) {
							lastTick = now;
							tick(gc);

						}
					}
				};

				timer.start();

				game.addEventFilter(KeyEvent.KEY_PRESSED, key -> {

					if (key.getCode() == KeyCode.UP && direction != Dir.down) {
						direction = Dir.up;
					}
					if (key.getCode() == KeyCode.LEFT && direction != Dir.right) {
						direction = Dir.left;
					}
					if (key.getCode() == KeyCode.DOWN && direction != Dir.up) {
						direction = Dir.down;
					}
					if (key.getCode() == KeyCode.RIGHT && direction != Dir.left) {
						direction = Dir.right;
					}

				});

				snake.add(new Corner(width / 2, height / 2));
				snake.add(new Corner(width / 2, height / 2));
				snake.add(new Corner(width / 2, height / 2));

				window.setScene(game);

			});

			Label Instructions = new Label("1. Use the arrow keys to move the snake\nup, down, left, and right."
					+ "\n\n2. If you hit the walls, the snake will die.\n\n3. Eat apples to grow, but don't crash into\n yourself or else you will die."
					+ "\n\n4. In easy mode, the snake's speed remains\nconstant. In hard mode, the higher your score,\nthe faster you will move."
					+ "\n\n5. Have Fun!");

			Instructions.setTextFill(Color.WHITE);
			Instructions.setFont(new Font("Verdana", 16));

			Instructions.setTranslateX(100);
			Instructions.setTranslateY(290);

			MenuButton btnInstructions = new MenuButton(" INSTRUCTIONS");
			gameOver = false;

			btnInstructions.setOnMouseClicked(event -> {
				getChildren().addAll(menu1, Instructions);

				TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), menu0);
				tt.setToX(menu0.getTranslateX() - offset);
				TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu1);
				tt1.setToX(menu0.getTranslateX());

				tt.play();
				tt1.play();

				FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), Instructions);

				fadeIn.setFromValue(0);
				fadeIn.setToValue(1);
				fadeIn.setAutoReverse(true);
				fadeIn.play();

				tt.setOnFinished(evt -> {
					getChildren().removeAll(menu0);

				});

			});

			MenuButton btnExit = new MenuButton(" EXIT");
			gameOver = false;

			btnExit.setOnMouseClicked(event -> {
				System.exit(0);

			});

			MenuButton btnBack = new MenuButton(" BACK");
			gameOver = false;

			btnBack.setOnMouseClicked(event -> {
				getChildren().addAll(menu0);
				TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), menu1);
				tt.setToX(menu1.getTranslateX() + offset);
				TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu0);
				tt1.setToX(menu1.getTranslateX());

				tt.play();
				tt1.play();

				FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), Instructions);
				fadeOut.setFromValue(1);
				fadeOut.setToValue(0);
				fadeOut.setAutoReverse(true);
				fadeOut.play();

				tt.setOnFinished(evt -> {
					getChildren().removeAll(menu1, Instructions);

				});

			});

			menu0.getChildren().addAll(btnEasy, btnHard, btnInstructions, btnExit);
			menu1.getChildren().addAll(btnBack);

			Rectangle rectangle = new Rectangle(800, 600);
			rectangle.setFill(Color.GREY);
			rectangle.setOpacity(0.3);

			getChildren().addAll(rectangle, menu0);

		}

	}
	
	public static void tick(GraphicsContext gc) {
		//gameover to stop timer, and display the gameover scene (check start method)
		if (gameOver) {
			
			timer.stop();
			window.setScene(over);
			return;

		}

		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i - 1).x;
			snake.get(i).y = snake.get(i - 1).y;

		}

		//Collision detection
		switch (direction) {

		//collision top
		case up:
			snake.get(0).y--;
			if (snake.get(0).y < 0) {
				gameOver = true;
			}
			break;

		//collision bottom
		case down:
			snake.get(0).y++;
			if (snake.get(0).y > height - 1) {
				gameOver = true;
			}

			break;

		//collision left
		case left:
			snake.get(0).x--;
			if (snake.get(0).x < 0) {
				gameOver = true;
			}
			break;

		//collision right
		case right:
			snake.get(0).x++;
			if (snake.get(0).x > width - 1) {
				gameOver = true;
			}
			break;
		
		//other
		case not:
			snake.get(0).x--;
			snake.get(0).x++;
			break;

		}

		//Snake eating food
		if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
			snake.add(new Corner(-1, -1));
			if (hard == true) {

				newFoodHard();
			} else if (hard == false) {
				newFoodEasy();
			}

		}

		// When snake hits itself, kill snake
		for (int i = 1; i < snake.size(); i++) {
			if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {

				gameOver = true;
			}
		}

		//Set background colour 
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width * cornersize, height * cornersize);

		// score counter
		if (hard == true) {
			gc.setFill(Color.WHITE);
			gc.setFont(new Font("", 30));
			gc.fillText("Score: " + (speed - 2), 10, 30);

		} else if (hard == false) {
			gc.setFill(Color.WHITE);
			gc.setFont(new Font("", 30));
			gc.fillText("Score: " + score, 10, 30);
		}
		

		//foodcolor is red
		Color cc = Color.RED;
		gc.setFill(cc);
		gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);


		// Actual snake
		for (Corner c : snake) {
			gc.setFill(Color.LIGHTGREEN);
			gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
			gc.setFill(Color.GREEN);
			gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);

		}
	}
	
	//method for food (HARD)
	public static void newFoodHard() {

		foodX = rand.nextInt(width);
		foodY = rand.nextInt(height);

		for (Corner c : snake) {
			if (c.x == foodX && c.y == foodY) {
				continue;
			}
		}
		speed++;
	}

	//method for food (EASY)
	public static void newFoodEasy() {

		foodX = rand.nextInt(width);
		foodY = rand.nextInt(height);

		for (Corner c : snake) {
			if (c.x == foodX && c.y == foodY) {
				continue;
			}
		}
		score++;
	}

	private static class MenuButton extends StackPane {
		public Text text;

		public MenuButton(String name) {
			text = new Text(name);
			text.getFont();
			text.setFont(Font.font(20));
			text.setFill(Color.WHITE);

			Rectangle rectangle = new Rectangle(250, 30);
			rectangle.setOpacity(0.6);
			rectangle.setFill(Color.BLACK);
			setAlignment(Pos.CENTER_LEFT);
			getChildren().addAll(rectangle, text);

			setOnMouseEntered(event -> {
				rectangle.setTranslateX(10);
				text.setTranslateX(10);
				rectangle.setFill(Color.WHITE);
				text.setFill(Color.BLACK);

			});

			setOnMouseExited(event -> {
				rectangle.setTranslateX(0);
				text.setTranslateX(0);
				rectangle.setFill(Color.BLACK);
				text.setFill(Color.WHITE);

			});
		}
	}

	public static void main(String[] args) {
		launch(args);

	}
}
