package com.nguyenanhtuyen.appthuexe;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nguyenanhtuyen.adapter.HangXeAdapter;
import com.nguyenanhtuyen.adapter.LoaiXeAdapter;
import com.nguyenanhtuyen.adapter.XeCoTaiXeAdapter;
import com.nguyenanhtuyen.model.HangXe;
import com.nguyenanhtuyen.model.LoaiXe;
import com.nguyenanhtuyen.model.XeCoTaiXe;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ThueXeCoTaiXeActivity extends AppCompatActivity {
    private ListView lvXeCoTaiXe;
    private XeCoTaiXeAdapter xeCoTaiXeAdapter, timXeCoTaiXeTheoTenAdapter, locXeCoTaiXeTheoLoaiXeAdapter, locXeCoTaiXeTheoHangXeAdapter, locXeCoTaiXeTheoGiaAdapter;
    private LoaiXeAdapter loaiXeAdapter;
    private HangXeAdapter hangXeAdapter;
    ImageView imgSearchXeCoTaiXe;
    TextView txtHienThiTatCaXeCoTaiXe;
    Button btnBoLocXeCoTaiXe, btnBanDoXeCoTaiXe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thue_xe_co_tai_xe);
        addControls();
        addEvents();
    }

    private void addEvents() {
        imgSearchXeCoTaiXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hienThiTimKiemXeCoTaiXeTheoTen(Gravity.TOP);
            }
        });
        txtHienThiTatCaXeCoTaiXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hienThiTatCaXeCoTaiXe();
            }
        });
        btnBoLocXeCoTaiXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hienThiBoLoc(Gravity.BOTTOM);
            }
        });
    }

    private void hienThiBoLoc(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_filter_xecotaixe);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }
        else{
            dialog.setCancelable(false);
        }

        //khai b??o c??c control
        ListView lvFilter_xecotaixe = dialog.findViewById(R.id.lvFilter_xecotaixe);
        SeekBar skXeCoTaiXe = dialog.findViewById(R.id.seekBarXeCoTaiXe);
        TextView txtLocTheoGiaXeCoTaiXe = dialog.findViewById(R.id.txtLocTheoGiaXeCoTaiXe);

        //add data lvFilter
        String[] arrData = {"Lo???i xe","H??ng xe","S???p x???p"};
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrData);
        lvFilter_xecotaixe.setAdapter(adapter);

        //x??? l?? s??? ki???n click lvFilter
        lvFilter_xecotaixe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(arrData[i].contains("Lo???i xe")){
                    hienThiDanhSachLoaiXe(Gravity.BOTTOM);
                    dialog.dismiss();
                }
                else if(arrData[i].contains("H??ng xe")){
                    hienThiToanBoHangXe(Gravity.BOTTOM);
                    dialog.dismiss();
                }
            }
        });

        //x??? l?? s??? ki???n change seekbar
        skXeCoTaiXe.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int a = i/1000;
                txtLocTheoGiaXeCoTaiXe.setText("B???t k??? - "+a+"K");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //x??? l?? l???c xe c?? t??i x??? theo gi?? call api
               LocXeCoTaiXeTheoGiaTask locXeCoTaiXeTheoGiaTask = new LocXeCoTaiXeTheoGiaTask();
               locXeCoTaiXeTheoGiaTask.execute(skXeCoTaiXe.getProgress());
                locXeCoTaiXeTheoGiaAdapter = new XeCoTaiXeAdapter(ThueXeCoTaiXeActivity.this, R.layout.item_xecotaixe);
                lvXeCoTaiXe.setAdapter(locXeCoTaiXeTheoGiaAdapter);
                if(skXeCoTaiXe.getProgress()==3000000){
                    txtLocTheoGiaXeCoTaiXe.setText("B???t k???");
                }
            }
        });

        dialog.show();
    }

    private void hienThiToanBoHangXe(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_hangxe_cotaixe);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }
        else{
            dialog.setCancelable(false);
        }

        //khai b??o c??c control
        ListView lvHangXeCoTaiXe = dialog.findViewById(R.id.lvHangXeCoTaiXe);

        //call api hi???n th??? to??n b??? h??ng xe
       HienThiToanBoHangXeTask hienThiToanBoHangXeTask = new HienThiToanBoHangXeTask();
       hienThiToanBoHangXeTask.execute();
        hangXeAdapter = new HangXeAdapter(this, R.layout.item_hangxe);
        lvHangXeCoTaiXe.setAdapter(hangXeAdapter);

        //x??? l?? s??? ki???n click item lvHangXeCoTaiXe
        lvHangXeCoTaiXe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //x??? l?? l???c xe c?? t??i x??? theo h??ng xe
                LocXeCoTaiXeTheoHangXeTask task = new LocXeCoTaiXeTheoHangXeTask();
               task.execute(hangXeAdapter.getItem(i).getMaHangXe());
                locXeCoTaiXeTheoHangXeAdapter = new XeCoTaiXeAdapter(ThueXeCoTaiXeActivity.this, R.layout.item_xecotaixe);
                lvXeCoTaiXe.setAdapter(locXeCoTaiXeTheoHangXeAdapter);
            }
        });

        dialog.show();
    }

    private void hienThiDanhSachLoaiXe(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_loaixe_xecotaixe);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity){
            dialog.setCancelable(true);
        }
        else{
            dialog.setCancelable(false);
        }

        //khai b??o c??c control
        ListView lvLoaiXeCoTaiXe = dialog.findViewById(R.id.lvLoaiXeCoTaiXe);

        //call api hi???n th??? to??n b??? lo???i xe
       HienThiToanBoLoaiXeTask hienThiToanBoLoaiXeTask = new HienThiToanBoLoaiXeTask();
        hienThiToanBoLoaiXeTask.execute();
        loaiXeAdapter = new LoaiXeAdapter(this, R.layout.item_loaixe);
        lvLoaiXeCoTaiXe.setAdapter(loaiXeAdapter);

        //x??? l?? s??? ki???n click item lvLoaiXeCoTaiXe
        lvLoaiXeCoTaiXe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //x??? l?? l???c xe c?? t??i x??? theo lo???i xe
               LocXeCoTaiXeTheoLoaiXeTask task = new LocXeCoTaiXeTheoLoaiXeTask();
               task.execute(loaiXeAdapter.getItem(i).getMaLoaiXe());
                locXeCoTaiXeTheoLoaiXeAdapter = new XeCoTaiXeAdapter(ThueXeCoTaiXeActivity.this, R.layout.item_xecotaixe);
                lvXeCoTaiXe.setAdapter(locXeCoTaiXeTheoLoaiXeAdapter);
            }
        });

        dialog.show();
    }

    private void hienThiTatCaXeCoTaiXe() {
       HienThiToanBoXeCoTaiXeTask hienThiToanBoXeCoTaiXeTask = new HienThiToanBoXeCoTaiXeTask();
        hienThiToanBoXeCoTaiXeTask.execute();
        xeCoTaiXeAdapter = new XeCoTaiXeAdapter(ThueXeCoTaiXeActivity.this, R.layout.item_xecotaixe);
        lvXeCoTaiXe.setAdapter(xeCoTaiXeAdapter);
    }

    private void hienThiTimKiemXeCoTaiXeTheoTen(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_timkiemxecotaixetheoten);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.TOP == gravity){
            dialog.setCancelable(true);
        }
        else{
            dialog.setCancelable(false);
        }

        //khai b??o c??c control
        EditText edtNhapTenXeCoTaiXeCanTim = dialog.findViewById(R.id.edtNhapTenXeCoTaiXeCanTim);
        Button btnTimKiemXeCoTaiXe = dialog.findViewById(R.id.btnTimKiemXeCoTaiXe);

        btnTimKiemXeCoTaiXe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TimKiemXeCoTaiXeTheoTenTask task = new TimKiemXeCoTaiXeTheoTenTask();
               task.execute(edtNhapTenXeCoTaiXeCanTim.getText().toString());
                timXeCoTaiXeTheoTenAdapter = new XeCoTaiXeAdapter(ThueXeCoTaiXeActivity.this, R.layout.item_xecotaixe);
                lvXeCoTaiXe.setAdapter(timXeCoTaiXeTheoTenAdapter);
            }
        });
        dialog.show();
    }

    class LocXeCoTaiXeTheoGiaTask extends AsyncTask<Integer, Void, ArrayList<XeCoTaiXe>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<XeCoTaiXe> xeCoTaiXes) {
            super.onPostExecute(xeCoTaiXes);
            locXeCoTaiXeTheoGiaAdapter.clear();
            locXeCoTaiXeTheoGiaAdapter.addAll(xeCoTaiXes);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<XeCoTaiXe> doInBackground(Integer... integers) {
            ArrayList<XeCoTaiXe> dsXe = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.1.50/dacn/api/xe/locxecotaixetheogia?gia="+integers[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json; charset-UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                JSONArray jsonArray = new JSONArray(builder.toString());
                for(int i=0; i<jsonArray.length();i++){
                    XeCoTaiXe xe = new XeCoTaiXe();
                    xe.setTenXe(jsonArray.optJSONObject(i).getString("TenXe"));
                    xe.setGiaThue(jsonArray.optJSONObject(i).getInt("Gia"));
                    xe.setHinhAnh(jsonArray.optJSONObject(i).getString("HinhAnh"));
                    xe.setMaXe(jsonArray.optJSONObject(i).getInt("MaXe"));
                    dsXe.add(xe);
                }
                br.close();
            }
            catch (Exception ex){
                Log.e("L???i",ex.toString());
            }
            return dsXe;
        }
    }

    class LocXeCoTaiXeTheoHangXeTask extends AsyncTask<Integer, Void, ArrayList<XeCoTaiXe>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<XeCoTaiXe> xeCoTaiXes) {
            super.onPostExecute(xeCoTaiXes);
            locXeCoTaiXeTheoHangXeAdapter.clear();
            locXeCoTaiXeTheoHangXeAdapter.addAll(xeCoTaiXes);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<XeCoTaiXe> doInBackground(Integer... integers) {
            ArrayList<XeCoTaiXe> dsXe = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.1.50/dacn/api/xe/locxecotaixetheohangxe?maHangXe="+integers[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json; charset-UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                JSONArray jsonArray = new JSONArray(builder.toString());
                for(int i=0; i<jsonArray.length();i++){
                    XeCoTaiXe xe = new XeCoTaiXe();
                    xe.setTenXe(jsonArray.optJSONObject(i).getString("TenXe"));
                    xe.setGiaThue(jsonArray.optJSONObject(i).getInt("Gia"));
                    xe.setHinhAnh(jsonArray.optJSONObject(i).getString("HinhAnh"));
                    xe.setMaXe(jsonArray.optJSONObject(i).getInt("MaXe"));
                    dsXe.add(xe);
                }
                br.close();
            }
            catch (Exception ex){
                Log.e("L???i",ex.toString());
            }
            return dsXe;
        }
    }

    class LocXeCoTaiXeTheoLoaiXeTask extends AsyncTask<Integer, Void, ArrayList<XeCoTaiXe>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<XeCoTaiXe> xeCoTaiXes) {
            super.onPostExecute(xeCoTaiXes);
            locXeCoTaiXeTheoLoaiXeAdapter.clear();
            locXeCoTaiXeTheoLoaiXeAdapter.addAll(xeCoTaiXes);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<XeCoTaiXe> doInBackground(Integer... integers) {
            ArrayList<XeCoTaiXe> dsXe = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.1.50/dacn/api/xe/locxecotaixetheoloaixe?maLoai="+integers[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json; charset-UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                JSONArray jsonArray = new JSONArray(builder.toString());
                for(int i=0; i<jsonArray.length();i++){
                    XeCoTaiXe xe = new XeCoTaiXe();
                    xe.setTenXe(jsonArray.optJSONObject(i).getString("TenXe"));
                    xe.setGiaThue(jsonArray.optJSONObject(i).getInt("Gia"));
                    xe.setHinhAnh(jsonArray.optJSONObject(i).getString("HinhAnh"));
                    xe.setMaXe(jsonArray.optJSONObject(i).getInt("MaXe"));
                    dsXe.add(xe);
                }
                br.close();
            }
            catch (Exception ex){
                Log.e("L???i",ex.toString());
            }
            return dsXe;
        }
    }

    class HienThiToanBoHangXeTask extends AsyncTask<Void, Void, ArrayList<HangXe>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<HangXe> hangXes) {
            super.onPostExecute(hangXes);
            hangXeAdapter.clear();
            hangXeAdapter.addAll(hangXes);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<HangXe> doInBackground(Void... voids) {
            ArrayList<HangXe> dsHangXe = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.1.50/dacn/api/hangxe/hienthitoanbohangxe");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json; charset-UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }

                JSONArray jsonArray = new JSONArray(builder.toString());
                for(int i=0; i<jsonArray.length();i++){

                    HangXe hangXe = new HangXe();
                    hangXe.setTenHangXe(jsonArray.optJSONObject(i).getString("TenNhaSanXuat"));
                    hangXe.setMaHangXe(jsonArray.optJSONObject(i).getInt("MaNhaSanXuat"));
                    dsHangXe.add(hangXe);
                }
                br.close();
            }
            catch (Exception ex){
                Log.e("L???i",ex.toString());
            }
            return dsHangXe;
        }
    }

    class HienThiToanBoLoaiXeTask extends AsyncTask<Void, Void, ArrayList<LoaiXe>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<LoaiXe> loaiXes) {
            super.onPostExecute(loaiXes);
            loaiXeAdapter.clear();
            loaiXeAdapter.addAll(loaiXes);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<LoaiXe> doInBackground(Void... voids) {
            ArrayList<LoaiXe> dsLoaiXe = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.1.50/dacn/api/loaixe/hienthitoanboloaixe");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json; charset-UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }

                JSONArray jsonArray = new JSONArray(builder.toString());
                for(int i=0; i<jsonArray.length();i++){

                    LoaiXe loaiXe = new LoaiXe();
                    loaiXe.setTenLoaiXe(jsonArray.optJSONObject(i).getString("TenLoaiXe"));
                    loaiXe.setMaLoaiXe(jsonArray.optJSONObject(i).getInt("MaLoaiXe"));
                    dsLoaiXe.add(loaiXe);
                }
                br.close();
            }
            catch (Exception ex){
                Log.e("L???i",ex.toString());
            }
            return dsLoaiXe;
        }
    }

    class TimKiemXeCoTaiXeTheoTenTask extends AsyncTask<String, Void, ArrayList<XeCoTaiXe>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<XeCoTaiXe> xeCoTaiXes) {
            super.onPostExecute(xeCoTaiXes);
            timXeCoTaiXeTheoTenAdapter.clear();
            timXeCoTaiXeTheoTenAdapter.addAll(xeCoTaiXes);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<XeCoTaiXe> doInBackground(String... strings) {
            ArrayList<XeCoTaiXe> dsXe = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.1.50/dacn/api/xe/timkiemxecotaixetheoten?ten="+strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json; charset-UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                JSONArray jsonArray = new JSONArray(builder.toString());
                for(int i=0; i<jsonArray.length();i++){
                    XeCoTaiXe xe = new XeCoTaiXe();
                    xe.setTenXe(jsonArray.optJSONObject(i).getString("TenXe"));
                    xe.setGiaThue(jsonArray.optJSONObject(i).getInt("Gia"));
                    xe.setHinhAnh(jsonArray.optJSONObject(i).getString("HinhAnh"));
                    xe.setMaXe(jsonArray.optJSONObject(i).getInt("MaXe"));
                    dsXe.add(xe);
                }
                br.close();
            }
            catch (Exception ex){
                Log.e("L???i",ex.toString());
            }
            return dsXe;
        }
    }

    class HienThiToanBoXeCoTaiXeTask extends AsyncTask<Void, Void, ArrayList<XeCoTaiXe>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<XeCoTaiXe> xeCoTaiXes) {
            super.onPostExecute(xeCoTaiXes);
            xeCoTaiXeAdapter.clear();
            xeCoTaiXeAdapter.addAll(xeCoTaiXes);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<XeCoTaiXe> doInBackground(Void... voids) {
            ArrayList<XeCoTaiXe> dsXe = new ArrayList<>();
            try {
                URL url = new URL("http://192.168.1.50/dacn/api/xe/hienthitoanboxecotaixe");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json; charset-UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }

                JSONArray jsonArray = new JSONArray(builder.toString());
                for(int i=0; i<jsonArray.length();i++){

                    XeCoTaiXe xe = new XeCoTaiXe();
                    xe.setMaXe(jsonArray.optJSONObject(i).getInt("MaXe"));
                    xe.setTenXe(jsonArray.optJSONObject(i).getString("TenXe"));
                    xe.setGiaThue(jsonArray.optJSONObject(i).getInt("Gia"));
                    xe.setHinhAnh(jsonArray.optJSONObject(i).getString("HinhAnh"));
                    xe.setMoTa(jsonArray.optJSONObject(i).getString("MoTa"));
                    xe.setMaNhaSanXuat(jsonArray.optJSONObject(i).getInt("MaNhaSanXuat"));
                    xe.setMaLoaiXe(jsonArray.optJSONObject(i).getInt("MaLoaiXe"));
                    xe.setMaThanhVien(jsonArray.optJSONObject(i).getInt("MaThanhVien"));
                    xe.setStatus(jsonArray.optJSONObject(i).getInt("Status"));
                    xe.setDiaChi(jsonArray.optJSONObject(i).getString("DiaChi"));
                    xe.setKinhDo(jsonArray.optJSONObject(i).getDouble("KinhDo"));
                    xe.setViDo(jsonArray.optJSONObject(i).getDouble("ViDo"));
                    dsXe.add(xe);
                }
                br.close();
            }
            catch (Exception ex){
                Log.e("L???i",ex.toString());
            }
            return dsXe;
        }
    }

    private void addControls() {
        //khai b??o c??c controll
        imgSearchXeCoTaiXe = findViewById(R.id.imgSearchXeCoTaiXe);
        txtHienThiTatCaXeCoTaiXe = findViewById(R.id.txtHienThiTatCa_CoTaiXe);
        btnBoLocXeCoTaiXe = findViewById(R.id.btnBoLoc_CoTaiXe);
        btnBanDoXeCoTaiXe = findViewById(R.id.btnBanDo_CoTaiXe);

        //hi???n th??? to??n b??? xe c?? t??i x??? call api
       HienThiToanBoXeCoTaiXeTask hienThiToanBoXeCoTaiXeTask = new HienThiToanBoXeCoTaiXeTask();
       hienThiToanBoXeCoTaiXeTask.execute();
        lvXeCoTaiXe = findViewById(R.id.lvToanBoXeCoTaiXe);
        xeCoTaiXeAdapter = new XeCoTaiXeAdapter(ThueXeCoTaiXeActivity.this, R.layout.item_xecotaixe);
        lvXeCoTaiXe.setAdapter(xeCoTaiXeAdapter);
    }
}