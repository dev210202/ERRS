package jkey20.errs.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFragment<B : ViewDataBinding, VM : ViewModel>(@LayoutRes private val layoutId: Int) :
    Fragment() {

    abstract val vm: VM
    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        with(binding) {
            lifecycleOwner = this@BaseFragment.viewLifecycleOwner
            // setVariable(BR.vm, vm)
        }
        return binding.root
    }
}