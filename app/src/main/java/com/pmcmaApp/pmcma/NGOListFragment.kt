package com.pmcmaApp.pmcma

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NGOListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ngoAdapter: NGOAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ngolist, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val ngos = listOf(
            NGO(
                name = "Greenfields",
                logo = R.drawable.greenfields_logo,  // Update to correct drawable resource
                description = "Greenfields: Cultivating a future of dignity and care for our elderly. Our objective is to provide affordable, residential accommodation for fit older persons over the age of 60. We specialize in providing Communal Living and essential support to able-bodied folk 60 and above. Beyond accommodation, Greenfields fosters a nurturing environment where our elderly residents can thrive, maintain independence, and engage in various activities.",
                images = listOf(
                    R.drawable.gf_one,
                    R.drawable.gf_two,
                    R.drawable.gf_three,
                    R.drawable.gf_four,
                    R.drawable.gf_five,
                    R.drawable.gf_six,
                    R.drawable.gf_seven,
                    R.drawable.gf_eight,
                    R.drawable.gf_nine,
                    R.drawable.gf_ten,
                    R.drawable.gf_eleven
                ),
                email = "contact@greenfields.org",
                phone = "+123 456 7890"
            ),
            NGO(
                name = "Phakamisa",
                logo = R.drawable.phakamisas_logo,
                description = "Phakamisa is a ministry of Pinetown Methodist Church near Durban, South Africa, dedicated to uplifting impoverished communities through educational training, resources, and support. Its programs focus on Early Childhood Development (ECD) and support for individuals living with HIV/AIDS.\n\nMission and Impact:\nOperating from Pinetown Methodist Church, Phakamisa provides practical education for children aged 0-6 years through its Edu-care and Childminder courses. The courses empower educators from disadvantaged backgrounds to teach children in various settings, including homes, churches, and even outdoors. Over 250 adults participate annually, impacting around 10,000 pre-school children. The organization also offers a Supervisor course for those running ECD centers, equipping them with essential skills for managing and improving these facilities.\n\nEducational Initiatives:\nPHAKAMISA's Wandering Teachers deliver free pre-school education to children in informal settlements. They use creative methods, teaching through rhythm, music, storytelling, and games. Due to the financial challenges faced by the centers, PHAKAMISA teaches students how to create educational tools from recycled materials, making learning accessible to all.\n\nCommunity Support:\nIn addition to education, PHAKAMISA runs a confidential HIV/AIDS support group that meets weekly. Participants engage in counseling, devotions, nutrition education, and social activities, promoting a positive and healthy lifestyle. The group also teaches vegetable gardening, essential for maintaining a nutritious diet in impoverished communities.\n\nHow to Support:\nDonations are vital to the organization's success. Contributions go directly to helping provide education, support, and resources to the children and adults in need.",
                images = listOf(
                    R.drawable.phajamisa_one,
                    R.drawable.phakamisa_two,
                    R.drawable.phajamisa_three,
                    R.drawable.phajamisa_four,
                    R.drawable.phajamisa_five,
                    R.drawable.phakamisa_six,
                    R.drawable.phakamisa_seven,
                    R.drawable.phakamisa_eight,
                    R.drawable.phakamisa_nine,
                    R.drawable.phakamisa_ten,
                    R.drawable.phakamisa_eleven
                ),
                email = "admin@phakamisa.org",
                phone = "031-7027308"
            ),
            NGO(
                name = "Sizanani",
                logo = R.drawable.sizananis_logo,
                description = "Sizanani is the isiZulu word for ‘help one another’. Sizanani is a Non-Profit Organisation that offers training in sewing and pattern making. The aim of Sizanani is to enable people who are unskilled and unemployed to obtain the required skills for self-employment. While Sizanani charges for its training, it is affordable for students who do not have the financial means to afford tertiary education.\n\nSizanani wants to equip students with a skill that will enable them to provide for their family, whether it is making clothing for personal use or creating articles that can be sold to bring in income.\n\nSizanani offers the following courses:\n\nFULL TIME STUDENT: 9am to 2pm – weekdays\n\n * 3 month – Basic Course\n * Year 1 – Advanced Course\n * Year 2 – Advanced Course\n\nPART TIME STUDENT: 9am to 12 noon – Saturdays\n\n * 10 month part-time course\n\nAttendance options for part-time students include Saturday mornings or weekday mornings (helpful for shift workers). The growth of personal dignity, self-esteem, and spiritual well-being is also encouraged.",
                images = listOf(
                    R.drawable.sizanani_one,
                    R.drawable.sizanani_two,
                    R.drawable.sizanani_three,
                    R.drawable.sizanani_four,
                    R.drawable.sizanani_five,
                    R.drawable.sizanani_six,
                    R.drawable.sizanani_seven,
                    R.drawable.sizanani_eight,
                    R.drawable.sizanani_nine,
                    R.drawable.sizanani_ten,
                    R.drawable.sizanani_eleven
                ),
                email = "Sizanani@pinetownmethodist.org.za",
                phone = "+27 31 7027 308"
            )
        )

        ngoAdapter = NGOAdapter(requireContext(), ngos)
        recyclerView.adapter = ngoAdapter

        return view
    }
}
