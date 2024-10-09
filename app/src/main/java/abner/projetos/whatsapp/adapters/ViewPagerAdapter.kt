package abner.projetos.whatsapp.adapters

import abner.projetos.whatsapp.fragments.ContatosFragment
import abner.projetos.whatsapp.fragments.ConversasFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    private val abas: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return abas.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return ContatosFragment()
        }
        return ConversasFragment()
    }
}