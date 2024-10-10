package abner.projetos.whatsapp

import abner.projetos.whatsapp.databinding.ActivityCadastroBinding
import abner.projetos.whatsapp.model.Usuario
import abner.projetos.whatsapp.utils.exibirMensagem
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    //Firebase Auth
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    //Fire Store
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarEventosCliques()
    }

    private fun inicializarEventosCliques() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(nome, email, senha)
            }

        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email, senha
        ).addOnCompleteListener { resultado ->
            if (resultado.isSuccessful) {

                val idUsuario = resultado.result.user?.uid
                if (idUsuario != null) {
                    val usuario = Usuario(
                        idUsuario, nome, email
                    )
                    salvarUsuarioFirestore(usuario)
                }

            }
        }.addOnFailureListener { erro ->
            try {
                throw erro
            } catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                erroSenhaFraca.printStackTrace()
                exibirMensagem("Senha fraca, digite outra letras, números e caracteres especiais")
            } catch (erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                erroUsuarioExistente.printStackTrace()
                exibirMensagem("E-mail já pertence a outro usuário")
            } catch (erroCredenciasInvalidas: FirebaseAuthInvalidCredentialsException) {
                erroCredenciasInvalidas.printStackTrace()
                exibirMensagem("E-mail ou senha incorretos")
            }
        }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        firestore
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao fazer seu cadastro")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                exibirMensagem("Erro ao fazer seu cadastro")
                Log.e("Erro", it.toString())
            }
    }

    private fun validarCampos(): Boolean {

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        if (nome.isNotEmpty()) {
            binding.textInputNome.error = null
            if (email.isNotEmpty()) {
                binding.textInputEmail.error = null
                if (senha.isNotEmpty()) {
                    binding.textInputSenha.error = null
                    return true
                } else {
                    binding.textInputSenha.error = "Preencha a sua senha!"
                    return false
                }
            } else {
                binding.textInputEmail.error = "Preencha o seu e-mail!"
                return false
            }
        } else {
            binding.textInputNome.error = "Preencha o seu nome!"
            return false
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}