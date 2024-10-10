package abner.projetos.whatsapp.fragments

import abner.projetos.whatsapp.activities.MensagemActivity
import abner.projetos.whatsapp.adapters.ContatosAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import abner.projetos.whatsapp.databinding.FragmentContatosBinding
import abner.projetos.whatsapp.model.Usuario
import abner.projetos.whatsapp.utils.Constantes
import android.content.Intent
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter

    //Firebase Auth
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    //Fire Store
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContatosBinding.inflate(inflater, container, false)

        contatosAdapter = ContatosAdapter { usuario ->
            val intent = Intent(context, MensagemActivity::class.java)
            intent.putExtra("dadosDestinatario", usuario)
            intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {

        eventoSnapshot = firestore
            .collection("usuarios")
            .addSnapshotListener { querySnapshot, error ->

                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents

                documentos?.forEach { documentSnapshot ->
                    val idUsuarioLogado = firebaseAuth.currentUser?.uid
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if (usuario != null && idUsuarioLogado != null) {
                        if (idUsuarioLogado != usuario.id) {
                            listaContatos.add(usuario)
                        }
                    }
                }
                if (listaContatos.isNotEmpty()) {
                    contatosAdapter.adicionarLista(listaContatos)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }
}