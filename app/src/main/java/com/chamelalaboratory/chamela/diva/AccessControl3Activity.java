/*
 * DIVA Android - Damn Insecure and Vulnerable App for Android
 *
 * Copyright 2016 © Payatu
 * Author: Aseem Jakhar aseem[at]payatu[dot]com
 * Websites: www.payatu.com  www.nullcon.net  www.hardwear.io www.null.co.in
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.chamelalaboratory.chamela.diva;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccessControl3Activity extends AppCompatActivity {

    private static final int MIN_PIN_LENGTH = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_control3);

        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
        String pin = spref.getString(getString(R.string.pkey), "");

        Button vbutton = findViewById(R.id.aci3viewbutton);
        if (!pin.isEmpty()) {
            vbutton.setVisibility(View.VISIBLE);
        } else {
            vbutton.setVisibility(View.GONE);
        }
    }

    public void addPin(View view) {
        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spedit = spref.edit();
        EditText pinTxt = findViewById(R.id.aci3Pin);
        String pin = pinTxt.getText().toString().trim();

        if (pin.length() < MIN_PIN_LENGTH) {
            Toast.makeText(this, "PIN should be at least " + MIN_PIN_LENGTH + " characters long!", Toast.LENGTH_SHORT).show();
        } else {
            spedit.putString(getString(R.string.pkey), pin);
            spedit.apply();

            Button vbutton = findViewById(R.id.aci3viewbutton);
            vbutton.setVisibility(View.VISIBLE);

            Toast.makeText(this, "PIN created successfully. Private notes are now protected with PIN.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToNotes(View view) {
        Intent i = new Intent(this, AccessControl3NotesActivity.class);
        startActivity(i);
    }
}
