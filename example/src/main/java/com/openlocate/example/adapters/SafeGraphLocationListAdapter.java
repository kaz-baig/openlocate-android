/*
 * Copyright (c) 2017 OpenLocate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.openlocate.example.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openlocate.example.R;
import com.openlocate.example.models.SafeGraphPlace;

import java.util.List;

public class SafeGraphLocationListAdapter extends RecyclerView.Adapter<SafeGraphLocationListAdapter.SafeGraphLocationViewHolder> {

    private List<SafeGraphPlace> places;

    public SafeGraphLocationListAdapter(List<SafeGraphPlace> places) {
        this.places = places;
    }

    @Override
    public SafeGraphLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_list_row, parent, false);
        return new SafeGraphLocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SafeGraphLocationViewHolder holder, int position) {
        holder.update(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class SafeGraphLocationViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView streetAddress;
        private TextView city;
        private TextView state;
        private TextView zipCode;
        private TextView naicsCategory;
        private TextView naicsSubCategory;
        private TextView naicsCode;
        private TextView distance;

        SafeGraphLocationViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.txt_name);
            this.streetAddress = (TextView) itemView.findViewById(R.id.txt_street_address);
            this.city = (TextView) itemView.findViewById(R.id.txt_city);
            this.state = (TextView) itemView.findViewById(R.id.txt_state);
            this.zipCode = (TextView) itemView.findViewById(R.id.txt_zip_code);
            this.naicsCategory = (TextView) itemView.findViewById(R.id.txt_naics_category);
            this.naicsSubCategory = (TextView) itemView.findViewById(R.id.txt_naics_subcategory);
            this.naicsCode = (TextView) itemView.findViewById(R.id.txt_naics_code);
            this.distance = (TextView) itemView.findViewById(R.id.txt_distance);
        }

        private void update(SafeGraphPlace place) {
            name.setText(place.getName());
            streetAddress.setText(place.getStreetAddress());
            city.setText(place.getCity());
            state.setText(place.getState());
            zipCode.setText(place.getZipCode());
            naicsCategory.setText(place.getNaicsCategory());
            naicsSubCategory.setText(place.getNaicsSubcategory());
            naicsCode.setText(place.getNaicsCode());
            distance.setText(String.valueOf(place.getDistance()));
            zipCode.setText(place.getZipCode());
        }
    }

}
