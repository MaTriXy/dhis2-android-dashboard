/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard.ui.fragments;

import static org.hisp.dhis.android.dashboard.R.id.view;
import static org.hisp.dhis.android.dashboard.api.utils.Preconditions.isNull;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.okhttp.HttpUrl;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import org.hisp.dhis.android.dashboard.R;
import org.hisp.dhis.android.dashboard.api.models.DataMap;
import org.hisp.dhis.android.dashboard.api.network.BaseMapLayerDhisTransformation;
import org.hisp.dhis.android.dashboard.api.utils.PicassoProvider;

import uk.co.senab.photoview.PhotoViewAttacher;

public class MapImageViewFragment extends BaseFragment {
    private static final String IMAGE_URL = "arg:imageUrl";

    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private CheckableImageButton viewBaseMapButton;
    private boolean modeWithBaseMap = true;
    private View rootView;

    public static MapImageViewFragment newInstance(String imageUrl) {
        isNull(imageUrl, "Image URL must not be null");

        Bundle arguments = new Bundle();
        arguments.putString(IMAGE_URL, imageUrl);

        MapImageViewFragment fragment = new MapImageViewFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private String getImageUrl() {
        String imageSimplePath = getArguments().getString(IMAGE_URL);
        return imageSimplePath;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_image_view, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewBaseMapButton =
                (CheckableImageButton) view.findViewById(R.id.view_base_map_button);


        viewBaseMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeWithBaseMap = !modeWithBaseMap;

                showMapImage(modeWithBaseMap);
            }
        });


        showMapImage(modeWithBaseMap);
    }

    private void showMapImage(boolean withBaseMap) {
        viewBaseMapButton.setSelected(withBaseMap);
        mImageView = (ImageView) rootView.findViewById(R.id.image_view_content);

        Context context = getActivity().getApplicationContext();

        String imageUrl = getImageUrl();

        if (withBaseMap) {


            DataMap dataMap = DataMap.getById(extractUid(imageUrl));

            PicassoProvider.getInstance(context, false)
                    .load(imageUrl)
                    .transform(new BaseMapLayerDhisTransformation(context, dataMap))
                    .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.OFFLINE)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .placeholder(R.mipmap.ic_stub_dashboard_item)
                    .into(mImageView);

            mAttacher = new PhotoViewAttacher(mImageView);
            mAttacher.setScale(7);
            mAttacher.update();
        } else {


            PicassoProvider.getInstance(context, false)
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.OFFLINE)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .placeholder(R.mipmap.ic_stub_dashboard_item)
                    .into(mImageView);

            mAttacher = new PhotoViewAttacher(mImageView);
            mAttacher.setScale(8);
            mAttacher.update();
        }
    }


    private String extractUid(String imageUrl) {
        HttpUrl url = HttpUrl.parse(imageUrl);

        String uid = url.pathSegments().get(3);

        return uid;
    }
}
