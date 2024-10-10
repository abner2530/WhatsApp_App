package abner.projetos.whatsapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import abner.projetos.whatsapp.databinding.ActivityMensagemBinding
import abner.projetos.whatsapp.model.Usuario
import abner.projetos.whatsapp.utils.Constantes
import android.os.Build
import com.squareup.picasso.Picasso

class MensagemActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagemBinding.inflate(layoutInflater)
    }

    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUsuarioDestinatario()
        inicializarToolbar()


    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagens.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            if (dadosDestinatario != null) {
                title = ""
                binding.tvNome.text = dadosDestinatario!!.nome
                Picasso
                    .get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imageFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUsuarioDestinatario() {
        val extras = intent.extras

        if (extras != null) {
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTATO) {
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(
                        "dadosDestinatario",
                        Usuario::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    extras.getParcelable(
                        "dadosDestinatario"
                    )
                }
            } else if (origem == Constantes.ORIGEM_CONVERSA) {

            }
        }
    }
}