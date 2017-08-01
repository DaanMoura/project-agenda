package com.alura.daniel.agenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alura.daniel.agenda.dao.AlunoDAO;
import com.alura.daniel.agenda.modelo.Aluno;

public class FormularioActivity extends AppCompatActivity {

    private FormularioHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");
        if (aluno != null){
            helper.preencheFormulario(aluno);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.pegaAluno();
                AlunoDAO dao = new AlunoDAO(this);
                if(aluno.getId() != null){
                    dao.altera(aluno);
                    dao.close();
                    Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + " salvo!", Toast.LENGTH_SHORT).show();
                }
                else{
                    dao.insere(aluno);
                    dao.close();
                    Toast.makeText(FormularioActivity.this, "Adicionado " + aluno.getNome() + "!", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}