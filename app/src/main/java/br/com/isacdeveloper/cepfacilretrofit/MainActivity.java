package br.com.isacdeveloper.cepfacilretrofit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import br.com.isacdeveloper.cepfacilretrofit.api.CEPService;
import br.com.isacdeveloper.cepfacilretrofit.model.CEP;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private CardView cardViewResultado;
    private Button botaoRecuperar;
    private TextView textoResultado;
    private Retrofit retrofit;
    private EditText edtCep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFormulario();

        botaoRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validaCampo()) {
                    recuperarCEPRetrofit();
                }
            }
        });

    }

    /**
     * Iniciar o formulario e carregar o retrofit
     */
    private void initFormulario() {
        //Adicionando visibilidade dos componentes
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);

        cardViewResultado = findViewById(R.id.cardViewResultado);
        cardViewResultado.setVisibility(View.INVISIBLE);

        botaoRecuperar = findViewById(R.id.btnRecuperarCep);
        textoResultado = findViewById(R.id.txtResultado);
        edtCep = findViewById(R.id.edtCep);

        //Cria o Objeto Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Método booleano de validação de campos
     *
     * @return
     */
    private boolean validaCampo() {
        if (edtCep.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Preencha o CEP Corretamente!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (edtCep.getText().toString().length() < 8 || edtCep.getText().toString().length() > 8) {
            Toast.makeText(MainActivity.this, "Digite o CEP com 8 numeros!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Aciona o Retrofit para requisição do CEP
     */
    private void recuperarCEPRetrofit() {

        //Cria a Variavel cep convertida em String para ser usada na URL
        String cep = edtCep.getText().toString();

        CEPService cepService = retrofit.create(CEPService.class);
        Call<CEP> call = cepService.recuperarCEP(cep);

        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if (response.isSuccessful()) {
                    CEP cep = response.body();
                    cardViewResultado.setVisibility(View.VISIBLE);
                    textoResultado.setText(cep.getLogradouro() + "\n" + cep.getCep() + "\n" + cep.getComplemento() + "\n" + cep.getBairro() + "\n" + cep.getLocalidade() + "\n" + cep.getUf());
                }
                progressBar.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onFailure(Call<CEP> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falha ao recuperar dados. Tente novamente!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}

