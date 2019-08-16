package com.flappybirdlucas.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle retanguloCanoBaixo;
	private Rectangle retanguloCanoTopo;
	//private ShapeRenderer shape;

	//Atributos de Conf
	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoJogo = 0; // 0->jogo não iniciado 1-> jogo iniciado 2-> Game Over
	private int pontuacao = 0;

	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoIncialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandom;
	private boolean marcouPonto = false;

	//Camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {

		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();
		/*retanguloCanoBaixo = new Rectangle();
		retanguloCanoTopo = new Rectangle();
		shape = new ShapeRenderer();*/
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(7);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");

		//Conf Camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH /2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;

		posicaoIncialVertical = alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 300;


	}

	@Override
	public void render () {

		camera.update();

		//Limpar Frames Anteriores Ajuda na memoria
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 7;

		if (variacao > 2) {
			variacao = 0;
		}

		if (estadoJogo == 0) {

			if (Gdx.input.justTouched()) {
				estadoJogo = 1;
			}

		} else {
			velocidadeQueda++;
			if (posicaoIncialVertical > 0 || velocidadeQueda < 0) {
				posicaoIncialVertical = posicaoIncialVertical - velocidadeQueda;
			}

			if (estadoJogo == 1) {
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;
				if (Gdx.input.justTouched()) {
					velocidadeQueda = -20;
					//Verifica se o cano saiu da tela
					if (posicaoMovimentoCanoHorizontal < -canoBaixo.getWidth()) {
						posicaoMovimentoCanoHorizontal = larguraDispositivo;
						alturaEntreCanosRandom = numeroRandomico.nextInt(400) - 200;
						marcouPonto = false;

					}

					//Verifica pontuacao
					if (posicaoMovimentoCanoHorizontal < 120) {
						if (!marcouPonto) {
							pontuacao++;
							marcouPonto = true;
						}
					}
				}
				} else {//Tela GameOver

					if (Gdx.input.justTouched()) {
						estadoJogo = 0;
						pontuacao = 0;
						velocidadeQueda = 0;
						posicaoIncialVertical = alturaDispositivo / 2;
						posicaoMovimentoCanoHorizontal = larguraDispositivo;
					}

				}


		}

		//Configurar dados de projeção da Camera
		batch.setProjectionMatrix(camera.combined);

			batch.begin();

			batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
			batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandom);
			batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandom);
			batch.draw(passaros[(int) variacao], 120, posicaoIncialVertical);
			fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 70);

			if(estadoJogo ==2){
			    batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
			    mensagem.draw(batch, "Toque para Reiniciar", larguraDispositivo / 2 - 200, alturaDispositivo / 2 - gameOver.getHeight() / 2);
            }

			batch.end();

			passaroCirculo.set(120 + passaros[0].getWidth() / 2 , posicaoIncialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
			retanguloCanoBaixo = new Rectangle(
					posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandom,
					canoBaixo.getWidth(), canoBaixo.getHeight()
								);
			retanguloCanoTopo = new Rectangle(
					posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandom,
					canoTopo.getWidth(), canoTopo.getHeight()
			);

			//Desenhar Formas
			/*shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
			shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
			shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
			shape.setColor(Color.RED);
			shape.end();*/

			//Teste Colisão
			if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
					 || posicaoIncialVertical <= 0 || posicaoIncialVertical >= alturaDispositivo){
				estadoJogo = 2;

			}

		}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
