package com.example.jubinjacob.twaingg

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.databinding.DataBindingUtil
import android.util.Log
import com.example.jubinjacob.twaingg.databinding.KnobCollectionFragmentBinding
import kotlinx.android.synthetic.main.knob_collection_fragment.*
import kotlin.properties.Delegates

/**
 * Created by jubinjacob on 10/11/17.
 */

class KnobCollectionFragment : Fragment() {
    private lateinit var gridLayoutManager: GridLayoutManager

    val rows = 7
    val columns = 5

    var count : Int by Delegates.observable(0) { property, old, new ->
        binding.setVariable(BR.count,new)
        binding.executePendingBindings()
    }

    lateinit var binding : KnobCollectionFragmentBinding
    lateinit var adaptor : KnobGridAdaptor

    var colorsMap = HashMap<Int,Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<KnobCollectionFragmentBinding>(activity,R.layout.knob_collection_fragment)
        binding.setVariable(BR.count,count)
        binding.executePendingBindings()
        RxBus.listen(ColorChangeEvent::class.java).subscribe {
            colorsMap[it.position] = it.color
            if (colorsMap.count() == rows*columns) {
                if (HashSet(colorsMap.values).count() == 1) {
                    Log.d("Game","You've won")
                }
            }
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = getView()?.findViewById<RecyclerView>(R.id.knobRecyclerView)
        gridLayoutManager = GridLayoutManager(activity, columns)
        recyclerView?.layoutManager = gridLayoutManager as RecyclerView.LayoutManager?
        adaptor = KnobGridAdaptor(rows = rows, columns = columns)
        adaptor.moveCounterEmitter.subscribe{
            count = it
        }
        recyclerView?.adapter = adaptor
    }

    fun reset() {
        adaptor.reset()
        adaptor.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.knob_collection_fragment, container, false)
    }
}