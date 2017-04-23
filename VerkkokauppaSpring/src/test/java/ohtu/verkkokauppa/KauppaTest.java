/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.verkkokauppa;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Samu
 */
public class KauppaTest {
    
    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;

    @Before
    public void setUp() {
        pankki = mock(Pankki.class);
        viite = mock(Viitegeneraattori.class);
        varasto = mock(Varasto.class);
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10); 
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // sitten testattava kauppa 
        Kauppa k = new Kauppa(varasto, pankki, viite);              

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(),anyInt());   
        // toistaiseksi ei välitetty kutsussa käytetyistä parametreista
    }
    
    @Test
    public void ostetaanTuoteJotaOn() {
        when(viite.uusi()).thenReturn(12);
        when(varasto.saldo(1)).thenReturn(14); 
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        Kauppa k = new Kauppa(varasto, pankki, viite);  
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        String name = "janne";
        String accnmb = "01234";
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(eq(name), anyInt(), eq(accnmb), anyString(), eq(5));
    }
    
    @Test
    public void ostetaanKaksiEriTuoteJotaOn() {
        when(viite.uusi()).thenReturn(12);
        when(varasto.saldo(1)).thenReturn(14);
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sipsit", 3));
        Kauppa k = new Kauppa(varasto, pankki, viite);  
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        String name = "mauri";
        String accnmb = "987654";
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(eq(name), anyInt(), eq(accnmb), anyString(), eq(8));
    }
    
    @Test
    public void ostetaanKaksiSamaTuoteJotaOn() {
        when(viite.uusi()).thenReturn(12);
        when(varasto.saldo(1)).thenReturn(14);
//        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
//        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sipsit", 3));
        Kauppa k = new Kauppa(varasto, pankki, viite);  
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        String name = "mauri";
        String accnmb = "987654";
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(eq(name), anyInt(), eq(accnmb), anyString(), eq(10));
    }
    
    @Test
    public void ostetaanKaksiToinenLoppu() {
        when(viite.uusi()).thenReturn(12);
        when(varasto.saldo(1)).thenReturn(14);
        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sipsit", 3));
        Kauppa k = new Kauppa(varasto, pankki, viite);  
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        String name = "mauri";
        String accnmb = "987654";
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(eq(name), anyInt(), eq(accnmb), anyString(), eq(5));
    }
    
    @Test
    public void aloitetaanUusiOstotapahtuma() {
        when(viite.uusi()).thenReturn(12);
        when(varasto.saldo(1)).thenReturn(14);
        when(varasto.saldo(2)).thenReturn(4);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sipsit", 3));
        Kauppa k = new Kauppa(varasto, pankki, viite);  
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.lisaaKoriin(2);
        
        String name = "mauri";
        String accnmb = "987654";
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(eq(name), anyInt(), eq(accnmb), anyString(), eq(6));
    }
    
    @Test
    public void uusiViitenumero() {
        when(viite.uusi()).
                thenReturn(1).
                thenReturn(2);
        when(varasto.saldo(1)).thenReturn(14);
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sipsit", 3));
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        String name = "mauri";
        String accnmb = "987654";
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(anyString(), eq(1), anyString(), anyString(),anyInt());
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.lisaaKoriin(2);
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(anyString(), eq(2), anyString(), anyString(),anyInt());
        
//        verify(viite, times(2)).uusi();
    }
    
    @Test
    public void koristaPoisto() {
        when(viite.uusi()).thenReturn(1);
        when(varasto.saldo(1)).thenReturn(14);
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "sipsit", 3));
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        String name = "mauri";
        String accnmb = "987654";
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.poistaKorista(1);
        k.tilimaksu(name, accnmb);
        
        verify(pankki).tilisiirto(eq(name), anyInt(), eq(accnmb), anyString(), eq(8));
    }
}
