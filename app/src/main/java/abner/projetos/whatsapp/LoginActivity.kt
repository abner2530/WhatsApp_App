package abner.projetos.whatsapp

import abner.projetos.whatsapp.databinding.ActivityLoginBinding
import abner.projetos.whatsapp.utils.exibirMensagem
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var senha: String

    //Firebase Auth
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
        //firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }


    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }

        binding.btnLogar.setOnClickListener {
            if (validarCampos()) {
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener {
            exibirMensagem("Logado com sucesso")
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }.addOnFailureListener { erro ->
            try {
                throw erro
            } catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("E-mail não cadastrado")
            } catch (erroCredenciasInvalidas: FirebaseAuthInvalidCredentialsException) {
                erroCredenciasInvalidas.printStackTrace()
                exibirMensagem("E-mail ou senha estão incorretos")
            }
        }
    }

    private fun validarCampos(): Boolean {

        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if (email.isNotEmpty()) {
            binding.textInputLoginSenha.error = null
            if (senha.isNotEmpty()) {
                binding.textInputLoginSenha.error = null
                return true
            } else {
                binding.textInputLoginSenha.error = "Preencha a e-mail"
                return false
            }
        } else {
            binding.textInputLoginSenha.error = "Preencha a senha"
            return false
        }
    }
}