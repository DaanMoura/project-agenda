package com.alura.daniel.agenda;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.alura.daniel.agenda.adapter.AlunosAdapter;
import com.alura.daniel.agenda.dao.AlunoDAO;
import com.alura.daniel.agenda.modelo.Aluno;

import java.util.List;
import java.util.jar.Manifest;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.view.Menu;
//import android.view.MenuItem;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, android.Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                    new String[]{android.Manifest.permission.RECEIVE_SMS}, 235);
        }

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intentVaiProFormulario.putExtra("aluno", aluno);
                startActivity(intentVaiProFormulario);
            }
        });

        Button novoALuno = (Button) findViewById(R.id.lista_novoAluno);
        novoALuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent VaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(VaiProFormulario);
            }
        });

        registerForContextMenu(listaAlunos);
    }

    private void carregaLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        AlunosAdapter adapter = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        MenuItem itemLigar = menu.add("Ligar");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, android.Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                            new String[]{android.Manifest.permission.CALL_PHONE}, 123);
                }
                else{
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:"+aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });


        MenuItem itemSMS = menu.add("SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:"+aluno.getTelefone()));
        itemSMS.setIntent(intentSMS);

        MenuItem itemVisitarSite = menu.add("Visitar site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);
        String site = aluno.getSite();
        if(!site.startsWith("http://")){
            site = "http://" + site;
        }
        intentSite.setData(Uri.parse(site));
        itemVisitarSite.setIntent(intentSite);

        MenuItem itemMapa = menu.add("Achar no Mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?="+aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);

        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);

                dao.deleta(aluno);
                dao.close();
                carregaLista();
                return false;
            }
        });
    }
}
