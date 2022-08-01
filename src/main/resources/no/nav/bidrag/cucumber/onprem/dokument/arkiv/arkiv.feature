# language: no
@bidrag-dokument-arkiv
Egenskap: bidrag-dokument-arkiv

  Tester REST API til endepunkt i bidrag-dokument-arkiv.

  Bakgrunn: REST grensesnitt for nais applikasjon bidrag-dokument-arkiv
    Gitt nais applikasjon 'bidrag-dokument-arkiv'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: bidrag-dokument-arkiv: Sjekk at henting av journal resulterer i ei tom liste (SAF-grensesnitt, testbruker må ha rolle GOSYS_NASJONAL)
    Når jeg kaller endpoint '/sak/1234567/journal' med parameter 'fagomrade' = 'BID'
    Så skal http status være 200
    Og så skal responsen være ei tom liste

  Scenario: bidrag-dokument-arkiv: Sjekk at distribusjon av journalpost går OK
    Gitt saksnummer '1000000' og fagområdet 'BID'
    Og at det finnes en utgående journalpost i arkiv på fagområdet og saksnummer
    Og kaller journalpost kan arkiveres endepunkt
    Så skal http status være 200
    Og bestiller distribusjon av Joark journalpost
    Så skal http status være 200

  Scenario: bidrag-dokument-arkiv - Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt fagområdet 'BID'
    Gitt alle søknadsoppgaver med saksnummer '2121212' er lukket
    Og opprettet joark journalpost på nøkkel 'JOARK_INNGAAENDE_JOURNALFOR':
          """
            {
              "tittel": "Bidrag automatisk test av registrer journalpost",
              "journalposttype": "INNGAAENDE",
              "tema": "BID",
              "behandlingstema": "ab0322",
              "kanal": "NAV_NO",
              "journalfoerendeEnhet": "0701",
              "avsenderMottaker": {
                "id": "27516441319",
                "idType": "FNR",
                "navn": "Blund, Jon"
              },
              "bruker": {
                "id": "27516441319",
                "idType": "FNR"
              },
              "dokumenter": [
                {
                  "tittel": "En cucumber test",
                  "brevkode": "NAV 04-01.04",
                  "dokumentvarianter": [
                    {
                      "filtype": "PDFA",
                      "fysiskDokument": "JVBERi0xLjMKJcTl8uXrp/Og0MTGCjMgMCBvYmoKPDwgL0ZpbHRlciAvRmxhdGVEZWNvZGUgL0xlbmd0aCAxODIgPj4Kc3RyZWFtCngBpY87C8JAEIR7f8X4vii57K55XFrFxi6wnbEKWAgpQv4/eIlRFFEQuWLuduc+ZhoUaBDtWkbVgvrTVn5EVuLbu7uwQypsc0FVY6sIyTK5HFqBOet9nYqQJSJBwjzSGpGqgKFnHGHGkwBhTjBTr5mDmQ069+pZZjHsl8O8NMOiDIJR77gTVp3D//CEE/SAvfY1vmd+DuoSZ/OYsvegZh1AL78TH9U3qXut/jdRJP1MLK6qe1nmCmVuZHN0cmVhbQplbmRvYmoKMSAwIG9iago8PCAvVHlwZSAvUGFnZSAvUGFyZW50IDIgMCBSIC9SZXNvdXJjZXMgNCAwIFIgL0NvbnRlbnRzIDMgMCBSIC9NZWRpYUJveCBbMCAwIDU5NSA4NDJdCj4+CmVuZG9iago0IDAgb2JqCjw8IC9Qcm9jU2V0IFsgL1BERiAvVGV4dCBdIC9Db2xvclNwYWNlIDw8IC9DczEgNSAwIFIgPj4gL0ZvbnQgPDwgL1RUMiA3IDAgUgo+PiA+PgplbmRvYmoKOCAwIG9iago8PCAvTiAzIC9BbHRlcm5hdGUgL0RldmljZVJHQiAvTGVuZ3RoIDI2MTIgL0ZpbHRlciAvRmxhdGVEZWNvZGUgPj4Kc3RyZWFtCngBnZZ3VFPZFofPvTe90BIiICX0GnoJINI7SBUEUYlJgFAChoQmdkQFRhQRKVZkVMABR4ciY0UUC4OCYtcJ8hBQxsFRREXl3YxrCe+tNfPemv3HWd/Z57fX2Wfvfde6AFD8ggTCdFgBgDShWBTu68FcEhPLxPcCGBABDlgBwOFmZgRH+EQC1Py9PZmZqEjGs/buLoBku9ssv1Amc9b/f5EiN0MkBgAKRdU2PH4mF+UClFOzxRky/wTK9JUpMoYxMhahCaKsIuPEr2z2p+Yru8mYlybkoRpZzhm8NJ6Mu1DemiXho4wEoVyYJeBno3wHZb1USZoA5fco09P4nEwAMBSZX8znJqFsiTJFFBnuifICAAiUxDm8cg6L+TlongB4pmfkigSJSWKmEdeYaeXoyGb68bNT+WIxK5TDTeGIeEzP9LQMjjAXgK9vlkUBJVltmWiR7a0c7e1Z1uZo+b/Z3x5+U/09yHr7VfEm7M+eQYyeWd9s7KwvvRYA9iRamx2zvpVVALRtBkDl4axP7yAA8gUAtN6c8x6GbF6SxOIMJwuL7OxscwGfay4r6Df7n4Jvyr+GOfeZy+77VjumFz+BI0kVM2VF5aanpktEzMwMDpfPZP33EP/jwDlpzcnDLJyfwBfxhehVUeiUCYSJaLuFPIFYkC5kCoR/1eF/GDYnBxl+nWsUaHVfAH2FOVC4SQfIbz0AQyMDJG4/egJ961sQMQrIvrxorZGvc48yev7n+h8LXIpu4UxBIlPm9gyPZHIloiwZo9+EbMECEpAHdKAKNIEuMAIsYA0cgDNwA94gAISASBADlgMuSAJpQASyQT7YAApBMdgBdoNqcADUgXrQBE6CNnAGXARXwA1wCwyAR0AKhsFLMAHegWkIgvAQFaJBqpAWpA+ZQtYQG1oIeUNBUDgUA8VDiZAQkkD50CaoGCqDqqFDUD30I3Qaughdg/qgB9AgNAb9AX2EEZgC02EN2AC2gNmwOxwIR8LL4ER4FZwHF8Db4Uq4Fj4Ot8IX4RvwACyFX8KTCEDICAPRRlgIG/FEQpBYJAERIWuRIqQCqUWakA6kG7mNSJFx5AMGh6FhmBgWxhnjh1mM4WJWYdZiSjDVmGOYVkwX5jZmEDOB+YKlYtWxplgnrD92CTYRm40txFZgj2BbsJexA9hh7DscDsfAGeIccH64GFwybjWuBLcP14y7gOvDDeEm8Xi8Kt4U74IPwXPwYnwhvgp/HH8e348fxr8nkAlaBGuCDyGWICRsJFQQGgjnCP2EEcI0UYGoT3QihhB5xFxiKbGO2EG8SRwmTpMUSYYkF1IkKZm0gVRJaiJdJj0mvSGTyTpkR3IYWUBeT64knyBfJQ+SP1CUKCYUT0ocRULZTjlKuUB5QHlDpVINqG7UWKqYup1aT71EfUp9L0eTM5fzl+PJrZOrkWuV65d7JU+U15d3l18unydfIX9K/qb8uAJRwUDBU4GjsFahRuG0wj2FSUWaopViiGKaYolig+I1xVElvJKBkrcST6lA6bDSJaUhGkLTpXnSuLRNtDraZdowHUc3pPvTk+nF9B/ovfQJZSVlW+Uo5RzlGuWzylIGwjBg+DNSGaWMk4y7jI/zNOa5z+PP2zavaV7/vCmV+SpuKnyVIpVmlQGVj6pMVW/VFNWdqm2qT9QwaiZqYWrZavvVLquNz6fPd57PnV80/+T8h+qwuol6uPpq9cPqPeqTGpoavhoZGlUalzTGNRmabprJmuWa5zTHtGhaC7UEWuVa57VeMJWZ7sxUZiWzizmhra7tpy3RPqTdqz2tY6izWGejTrPOE12SLls3Qbdct1N3Qk9LL1gvX69R76E+UZ+tn6S/R79bf8rA0CDaYItBm8GooYqhv2GeYaPhYyOqkavRKqNaozvGOGO2cYrxPuNbJrCJnUmSSY3JTVPY1N5UYLrPtM8Ma+ZoJjSrNbvHorDcWVmsRtagOcM8yHyjeZv5Kws9i1iLnRbdFl8s7SxTLessH1kpWQVYbbTqsPrD2sSaa11jfceGauNjs86m3ea1rakt33a/7X07ml2w3Ra7TrvP9g72Ivsm+zEHPYd4h70O99h0dii7hH3VEevo4bjO8YzjByd7J7HTSaffnVnOKc4NzqMLDBfwF9QtGHLRceG4HHKRLmQujF94cKHUVduV41rr+sxN143ndsRtxN3YPdn9uPsrD0sPkUeLx5Snk+cazwteiJevV5FXr7eS92Lvau+nPjo+iT6NPhO+dr6rfS/4Yf0C/Xb63fPX8Of61/tPBDgErAnoCqQERgRWBz4LMgkSBXUEw8EBwbuCHy/SXyRc1BYCQvxDdoU8CTUMXRX6cxguLDSsJux5uFV4fnh3BC1iRURDxLtIj8jSyEeLjRZLFndGyUfFRdVHTUV7RZdFS5dYLFmz5EaMWowgpj0WHxsVeyR2cqn30t1Lh+Ps4grj7i4zXJaz7NpyteWpy8+ukF/BWXEqHhsfHd8Q/4kTwqnlTK70X7l35QTXk7uH+5LnxivnjfFd+GX8kQSXhLKE0USXxF2JY0muSRVJ4wJPQbXgdbJf8oHkqZSQlKMpM6nRqc1phLT4tNNCJWGKsCtdMz0nvS/DNKMwQ7rKadXuVROiQNGRTChzWWa7mI7+TPVIjCSbJYNZC7Nqst5nR2WfylHMEeb05JrkbssdyfPJ+341ZjV3dWe+dv6G/ME17msOrYXWrlzbuU53XcG64fW+649tIG1I2fDLRsuNZRvfbore1FGgUbC+YGiz7+bGQrlCUeG9Lc5bDmzFbBVs7d1ms61q25ciXtH1YsviiuJPJdyS699ZfVf53cz2hO29pfal+3fgdgh33N3puvNYmWJZXtnQruBdreXM8qLyt7tX7L5WYVtxYA9pj2SPtDKosr1Kr2pH1afqpOqBGo+a5r3qe7ftndrH29e/321/0wGNA8UHPh4UHLx/yPdQa61BbcVh3OGsw8/rouq6v2d/X39E7Ujxkc9HhUelx8KPddU71Nc3qDeUNsKNksax43HHb/3g9UN7E6vpUDOjufgEOCE58eLH+B/vngw82XmKfarpJ/2f9rbQWopaodbc1om2pDZpe0x73+mA050dzh0tP5v/fPSM9pmas8pnS8+RzhWcmzmfd37yQsaF8YuJF4c6V3Q+urTk0p2usK7ey4GXr17xuXKp2737/FWXq2euOV07fZ19ve2G/Y3WHruell/sfmnpte9tvelws/2W462OvgV95/pd+y/e9rp95Y7/nRsDiwb67i6+e/9e3D3pfd790QepD14/zHo4/Wj9Y+zjoicKTyqeqj+t/dX412apvfTsoNdgz7OIZ4+GuEMv/5X5r0/DBc+pzytGtEbqR61Hz4z5jN16sfTF8MuMl9Pjhb8p/rb3ldGrn353+71nYsnE8GvR65k/St6ovjn61vZt52To5NN3ae+mp4req74/9oH9oftj9MeR6exP+E+Vn40/d3wJ/PJ4Jm1m5t/3hPP7CmVuZHN0cmVhbQplbmRvYmoKNSAwIG9iagpbIC9JQ0NCYXNlZCA4IDAgUiBdCmVuZG9iagoyIDAgb2JqCjw8IC9UeXBlIC9QYWdlcyAvTWVkaWFCb3ggWzAgMCA1OTUgODQyXSAvQ291bnQgMSAvS2lkcyBbIDEgMCBSIF0gPj4KZW5kb2JqCjkgMCBvYmoKPDwgL1R5cGUgL0NhdGFsb2cgL1BhZ2VzIDIgMCBSID4+CmVuZG9iago3IDAgb2JqCjw8IC9UeXBlIC9Gb250IC9TdWJ0eXBlIC9UcnVlVHlwZSAvQmFzZUZvbnQgL0FBQUFBQytDYWxpYnJpLUxpZ2h0IC9Gb250RGVzY3JpcHRvcgoxMCAwIFIgL1RvVW5pY29kZSAxMSAwIFIgL0ZpcnN0Q2hhciAzMyAvTGFzdENoYXIgNDMgL1dpZHRocyBbIDQ4MyA0OTQgMzg3CjMyOSA1MjAgNTIxIDQ0MSA1MjAgNzkxIDUyMCAyMjYgXSA+PgplbmRvYmoKMTEgMCBvYmoKPDwgL0xlbmd0aCAyODMgL0ZpbHRlciAvRmxhdGVEZWNvZGUgPj4Kc3RyZWFtCngBXZHNasMwEITveoo9podgxYmTBoyhpAR86A91+wC2tA6CWhayfPDbd6SkKfQwh29nR+yuslP9XFsTKHv3o2o4UG+s9jyNs1dMHV+MFZuctFHhRqmmhtaJDOFmmQIPte1HKktBlH0gMgW/0OpJjx0/xNqb1+yNvdDq69SkSjM7980D20BSVBVp7vHcS+te24EpS9F1reGbsKyR+uv4XBwTJkJicx1JjZon1yr2rb2wKKWsyvO5Emz1P+t4DXT9rTPfVGWUlMWuEmWeAyEp90XELXAX8bCNWAAhuKl5D4SAfXQPQAjYRXwEQsimp47ANrk6uh0QkjKXac7fieLI8bT3U6jZe1wh3T8dKC5uLN+/yI0uLpr0A/nRibkKZW5kc3RyZWFtCmVuZG9iagoxMCAwIG9iago8PCAvVHlwZSAvRm9udERlc2NyaXB0b3IgL0ZvbnROYW1lIC9BQUFBQUMrQ2FsaWJyaS1MaWdodCAvRmxhZ3MgNCAvRm9udEJCb3gKWy01MTEgLTI2OSAxMzA5IDk1Ml0gL0l0YWxpY0FuZ2xlIDAgL0FzY2VudCA5NTIgL0Rlc2NlbnQgLTI2OSAvQ2FwSGVpZ2h0IDYzMgovU3RlbVYgMCAvWEhlaWdodCA0NjIgL0F2Z1dpZHRoIDUyMCAvTWF4V2lkdGggMTM1MCAvRm9udEZpbGUyIDEyIDAgUiA+PgplbmRvYmoKMTIgMCBvYmoKPDwgL0xlbmd0aDEgNjYxMiAvTGVuZ3RoIDQ0NzEgL0ZpbHRlciAvRmxhdGVEZWNvZGUgPj4Kc3RyZWFtCngBzThrVBvXmffOSDNCQkIDknjI9owYEGAe4mVepkiAHoDABsPUwgRbYJ62MQRscJxgE7exseI03u02yTZNnCYkPidskxFps5g0u16a3Tp7aienJ013s7VPtqc/tueUTdJterp2QPvdkUTspO2P/tor3fnu97r3e917Rzo2eXwQ6dAcolHxwbG+CaQ0w8cAig5OHxOiuHoAIeqpoYnhsSjOfoYQ86PhIw8MRfGkWoS0EyODfSCnNOCjihEgRFFcDjBrZOzYiShuuAqw+Mj4wRg/SQA8Y6zvRGx99AvAhaN9Y4MAoaX/HTxyJyYHY3wcQEilVVh/7oGBmYg6kEoRopARORBYTK0o8yNE+ExZ2UcX1LsOJNV+iqwaRfDtXzyjrPOzJ0I9d369/oH2BPssMBIQpbAVPfbZ9Q/A5+t3fn1Hoz2hzBRjKiAxnOCoN+Ba5IRO4WpcgaoQj6tisBJXLFXx79bXAY5xE6rCPiRhL0APQDfARoANAOsBugA6AToAFgEsBFiAJDSH/bDwHG6BOZqjPLQbZsLoY5AH5/BXUDF0MBueE9DnoH8IXRW5ir/ymoHzIjCyHITKgVWO3oUOscJlSIA+h8uW6ERHfSIugcmS4MlDH4d+GvrjuGRJnYSWcZHraWz65UKE/89whP+Pv7mf//efF/EfvD/K/9t7z/A/f7+A/9n7lfy7NzL4d26M8NdvvML/5MYcn3QDj/E3MICd/3rtDP/2tQv8j69V8P+yuov/59UA/9bqAf5Hq0P8P62O8VdX53i0alwVVukxYbV4lSJDilConcIq/scVO/8PKzX8myt7+B+ujPFvrEzxV1a+xi+vPMC3r+DlyNXXVk6e8Spw5GgUSvuisMFLoCuy4ij1/iDs578f7uVfCx/kl8KHeDk8w78aPsO/Eu7hX1q4n39x4ST/wsIF/tIzW/lnn6nmv/PMN/m/fUrin7pQzIfweekROp//Ou3jz/TMSQ8vzkmne2alU4uzkmMWO2ads+Ozl2bfmY3MMg/0zEgnFmckfubxmUsz9Aw+KT3Uc1J6cPGkNHESz/eclc4tnpX4s4+fvXSWPktPSu3TwWlqGgZHh8YkeQwfGBsfOz1GjwHlWM+kNLU4Kbkmg5MTk3OTqklqXLq/Z1yaWByXxhnMHwKjRn3D0sjisDTkG5AGFwekg75+qc8XlFxBfJ9vn9SzuM/VIu0Fwa/6uiRpsUvq9HVIexY7pA7Gy+/27ZJ20dl8m88vtS76pRZfk9S82CQ1Ufm8z+eVvDifz8rU8mJmGo9orKHfxKTw1FCRF1FXvn+Zjezxy5r2HhnPy9md5Onq2Ccz8zKS9vUEwhh/o3spA1ONXaK/Y18Uf+Sxx9DWBr+8tTOwRD/33NaGbr88R8YulzKOkDECke79U/lfbnfTyHgKHlPwIV8FHNvUwTBC0AkkjYzz8+N8fHz/1DHoCjEqB4QoDpIxZZyP9k8pdFjkGCxG1lMagcfS9iPEPqseo5c3vklOBrWBnBt/QfOh/WgavYD2oKm/QHtTRT2NdHQYMSgFocjtyNrGS9CX1Ya7KIqdqq1A4aJqkYTIf4NEikJLUvQ+Id5sLNMNiFF0des/htk+BV42nQCa+kgF4P9DnSPjqAb77MarG5cVrk5ViZ+GOHSiGlSPWlEX+CahA+gwOgV4C4z70UmAPegIOo7OoEbkR18F/hAagxicRt+BOFxFPwVNCTSH0QSaAc1z6CJ6Ei2it1EvGgDJY+hBFELfRW+iEXQ/zPc1dAE9Bfze2DzfAziBHkBz6Dz6K/Rt4DQBj6y4G+RPwBwhdAhWuwh6MyD7/66p9yEzei7yh0jDxvPrP6Sz6UT8E/D5HHh0HHy5jfrVJnRYXRD5Pc6MfKLWRn6jOh35BJdEfou09EP0kMs1GJC6Ovd0tO/e1dbqb2lu8nk97saGepez7iu1O2uqqyordjiKCgty7dlZYiafZuKMSXqdNkHDMmoVTWFU4BG9QUG2B2WVXWxqKiS42AeEvrsIQVkAkvdeGVkgen3AukfSBZJDX5B0RSVdm5LYKNSi2sICwSMK8nW3KCzjfR0BGD/mFrsFeU0ZtyljlV1B9IDYbKAheNJG3IKMg4JH9k6PhDxBd2EBDuu0jWLjoLawAIW1OhjqYCTnihNhnFuHlQGV66kJU0ijJ8vKdLanb0Bu7wh43FabrVuhoUZlLplplFllLmFUBpvRo0K44GrowrIR9QfzEwfEgb77AjLdB0oh2hMKnZO5fDlPdMt5J3+VBgEclAtEt0fOF8Ew/57NBbCszjaKQuhTBMaLa78Bq++i9MUoTLbxU0SYxMXNMMm4Lz5GYBtYCP7ZbMSWR5ddqB8Qea4jEMUF1G9dQi5HfrdMBQnnapxjlghnLs7ZVA+KEFmP6AnGvtMjafJcv1BYAJlVvtmyKhv4gkzbg/0HRwjsGwyJbvAQYom6ArLLDQNXXyyYnnCxA+T7guDEKAlDR0B2iBOySWyIRhsIMEm2Z7QzoKhEqR7Z1Cij4MGYluzwgC6UiCdEEkMMJHOJHYErqCzyYbhcsL5WhspRN7FDtjRCUuyeUGBgSOaD1gGozyEhYLXJrm4IX7cYGOwmWRKNct6HsBw0SKCiBb59QTouDG7LbLZGCFBWuptkCwiCFx5iQy0wjDITRUlGG2qFALaiuBisEpMgo3vmAYTObmwCZYCg2thktUFxK+3PmGSNOgBmyJpNm1RghPpzm6Lr/EnTotLEoDzBM+i+y8B7JgVEMTA22x+3kyKxiAUDTNCQdDYRHwoLKBgLwNbIFPipkEgW0wQZtQsBcVDsFqGGXO0BkhwSayW//k7lDULJdqxKugJ3Y1F+1SYvNpLh3SMge/OVpJKcKrhPwTfRpi+wm+NsIaQR/Z0h8u4ixiZEAmwfyAxjb+57tCq5HHaqF05J0dsnCkbBG+pbjsz1h8IuV2jCExypgT0QEpsHQmJnoBYSqWz6WetJsnQy8mN/V0NhARw8DWERz3eEXXi+c1/gihEhYb4rsETBm1OwoTucBbzAFQEhl0KlCJUQiYhAEDLTHkA0irz1iguhOYWrUggKfnAZI4UWFQIaRgeXqSjNGJejgKaK0lwKrRsabK+0EYg/HMIeYYDk5qHukVCwm+wsZIE8whfLWKxDMiXWhTHFJMpacbBB1okNhO4kdGeUzhA6KzbI2ILhGPaMiPFYiZ6RvsEleLU0p4hVYYy4ikKkMqAi/A5C9C3EqXLRYfp3aIQ+hy4zXegy4JdVBWiATkMS9V2UA/gLCIFX5Hcf+WXIIAGgDX7/UvC7JwFp4VewBl5edcBhY1IIfQs+7+OD+BbVQf0vHaSvqfapfqquAk0VKoKVr8MbEA3y1agNtbiyClmqZslt+F6qGz4UFl7RqDDFazAt7mDojnSu2anCyHHz1s1b153QueTqauy4efPWGhk5HMY1+Ny8UVyCORundJOBYlkTI2YWUTtyyivKykrrqB3ldjHTQCm08orKOrqsdBtFg2SUUkcp+PXP+ujOz35PPZxVHyhXW8xag1atyjCbSupzuUB/jrNYYGlWTas1bG5lo80z1Jj5IWNI5ZLTkhgmKS2ZSzXA293t36oNd6pUZ+6coi21+10iflnDUiq16lqq2Vrkymq/jzNzdEJSYmKKhk1J1tsb9q0/FJ8hBknMuchtJVKZKMMFkV9N45jkla0diRJyOtdKseOt9evgM/HAxsX94GzE53Lwxkbcu67SJLIbpxhDGselGZiNUxqdRqWCB32KTYTR+3qTnr0zxWgZlYo8HmOBYDAbWNZghgwfjnxEP6weQ3bI1IwrsTLXZEGt06VnS6nC5cjHr1lQG8A/uGrNqG264GwBlWs0g8BwBk4wp5upBGO6kdLorNVhQ/n2NBVyLKXU2Ly66hyryrB9Ia2lfMHQpm4lzjjXklOrndh4a+291Oq10urikv29vb35vfnwzY67toOJJpQ4V2pJ5RS4jTLTdiWxZtM2oNdRlWCx3pRkNOvZpm/39If25pb2XzywZ75BTfKUqmdm/I+2evrrtqTVBJuy3E1tBSZWy9A0o2XP7OpuPfNq/9SVM95mH2WLR2V9V0+w4ci81/+N8UZTUXMF/B2CRiAy5+lVlA/X4UFX/XTJ2RLqRBF+Mu9yHvVE7ku5VIKYLlIJfDpPaYbYaZZKSbEVLCH4CVSuWrLtKHhBpdriWLC3pC0YMhMWtrQh5/rvSBiqHWskDKU3e8m4uKSXBAIahmq2xTwkhWo2Ge6KBuA5FUoUWPq8PXn9SVvryW7nQLNDCz7QFPhW3jXuGn3+aPXOo08fGLrYm/8gHfq6e9hnpyk2y9o131dusVoYCBsLL6va9HST88HXT0y9frrRPfVUl+7i82Vd47VQkZehIrvVk+Czz5VnKcElH2Ymhi3iDzIKcWGhRfv9dJCxpGbiVB3O1Klzm7d4uVYlw04n7FTwwvGe4lfvmnG99FfFJag3nt0c/HkJxxPL4egmNZsYFmOLhe6GOk4xZaTomY0Ao08lu00dzyqe1XCmDI5LN2oSkzaewH+t11pYLasieaWo9Y14MtVuINA0pPyzq/jhBB1LU0wCq01J2nh5Q0iCW0LxUh2G3LZDzbf72rC9raKNQm3GNkrtMXuoHHelm2LcFjel8Tlxhd1rp7zJ2JsS3ratVBvm23e3U6gdt9flhEuNC/6WrIX6ljpvYVVzYWv6XcEgRY8da9Xv9faucZB1qHlIcyzb8ajs4MqL4Hxi2LuC8yVC9OQymysqSGGkxmKmDkOEoN4N6tQSf5nz4TaCkoClKWjr5/Ezc5wlUb3rgqe6212c5Oj0ezOl4z7eFI8YVega9ef1H1if+dMU+nw8qnN7d1lL3HnlvvyU2qH51lg86UcgnqWo3pVHF6cUUxqLw+6ocNAVyd5kajtEThcm/75tMy4UtmxPz2qOByq5mgQJasYYjc/+zXr5Y5G5NxBm+pFoBPRMRnlHhSf0ZZefaOk+6bd97mh61xccvcctcCcIO38Adv418CYF5aD7XK7R7AeyqSHbtI0ask5bqdHUB1Kpc8n4SQ4z3FmO0ujxvAYnsOdZioU/e0xLLuLnkjY3fSGpRXxRDRufFAI5+XrXoseest+VLX/v3cQqtWCArR896OhrVeMvHT106fCOyvGXxg9dOlRxapt3OtAx7c+KQ/zLI6+f9TfOLk0cef2RlobZpZNtFw47a4YvdOx67FBdzdAFMEaKfES9Af40oyFX3bmmJ5uoDDvW2bGZwioK18tZWaWlidYwSWRiS9FCpfZFLlg5UUm1c7iSq+QstQv1VnVei2Uh6gvsc6hnuJV7e8m9DJ5Ft3ypA9xTXFNurMwilXJLQcVGL+ciJoYzcLQpdzK5rcFXhnqjuPshv6PL7bBoySWl2+7cW13UWsXn1nfu7azPzfIfb89srMw1w1ammQRGI5Q3ORy+otTchq69XQ25WF0/3GRPSs0wbUvXmwzsFtsWU65z+/aafJu4vbZrp6OnqUCXbDbqEjm4kQ2sJcNiya0QCmoLMjPzajqI4zmRT6ge1cvw34vkqtIa9camfO1OrV9L67VbtZRYGE5FeqOe0uu5vDyUxPGck9vNqTRc4YLYvFW/kNpcssBGM32dFPT+3rdKHXC895atlZKgkDtOpbyGcGKmXYlDWTw6ZnP0uGdEkTNZSKwITvWwSVvt202N+2ut5wxGuNHZc/GT8DYLdzynv71zV1rONrNGrVHTLeaMJH2COqt5opUqS0/Xwwl6K75hbzGApqev3xw+ok3UqnQc+PsCuffpv4cd63TlaLkMjsozVhspndFKLvQcZbPy4ZTC5hydOr056/N7HLLvhFMNUh67wzdPsvjtvXltc0oZV1RuEuDeNliMyeQs+payMeMOKdv3fBugcOLrmc39CkdN7fB8kBLjrqz/V9chclJRm6cRvHFilAydNAaZEKonrTG/se/IaP/kaGHr6PDIMYT+D/hHAWkKZW5kc3RyZWFtCmVuZG9iagoxMyAwIG9iago8PCAvVGl0bGUgKE1pY3Jvc29mdCBXb3JkIC0gRG9rdW1lbnQxKSAvUHJvZHVjZXIgKG1hY09TIFZlcnNqb24gMTIuNSBcKGJ5Z2cgMjFHNzJcKSBRdWFydHogUERGQ29udGV4dCkKL0NyZWF0b3IgKFdvcmQpIC9DcmVhdGlvbkRhdGUgKEQ6MjAyMjA4MDEwNjU4MjNaMDAnMDAnKSAvTW9kRGF0ZSAoRDoyMDIyMDgwMTA2NTgyM1owMCcwMCcpCj4+CmVuZG9iagp4cmVmCjAgMTQKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMjc2IDAwMDAwIG4gCjAwMDAwMDMyMjQgMDAwMDAgbiAKMDAwMDAwMDAyMiAwMDAwMCBuIAowMDAwMDAwMzgwIDAwMDAwIG4gCjAwMDAwMDMxODkgMDAwMDAgbiAKMDAwMDAwMDAwMCAwMDAwMCBuIAowMDAwMDAzMzU2IDAwMDAwIG4gCjAwMDAwMDA0NzcgMDAwMDAgbiAKMDAwMDAwMzMwNyAwMDAwMCBuIAowMDAwMDAzOTIwIDAwMDAwIG4gCjAwMDAwMDM1NjQgMDAwMDAgbiAKMDAwMDAwNDE2MSAwMDAwMCBuIAowMDAwMDA4NzIwIDAwMDAwIG4gCnRyYWlsZXIKPDwgL1NpemUgMTQgL1Jvb3QgOSAwIFIgL0luZm8gMTMgMCBSIC9JRCBbIDw0Nzk0MDNhMzA5ZGQyZTY5NDk4ODVlYTljZjQzMDBiND4KPDQ3OTQwM2EzMDlkZDJlNjk0OTg4NWVhOWNmNDMwMGI0PiBdID4+CnN0YXJ0eHJlZgo4OTMzCiUlRU9GCg==",
                      "variantformat": "ARKIV"
                    }
                  ]
                }
              ]
            }
          """
    Og skal ha totalt 1 åpne journalføringsoppgaver
    Og skal responsen fra oppgave med type 'JFR' inneholde feltet 'tildeltEnhetsnr' = '4812'
    Og skal responsen fra oppgave med type 'JFR' inneholde feltet 'aktoerId' = '2616400414139'
    Og jeg registrerer endring på opprettet journalpost på enhet '4806' med nøkkel 'JOARK_INNGAAENDE_JOURNALFOR':
      """
      {
        "skalJournalfores":true,
        "gjelder": "22466401394",
        "tittel":"Journalfør cucumber test",
        "tilknyttSaker":["2121212"]
      }
      """
    Så skal http status være 200
    Og skal ha totalt 0 åpne journalføringsoppgaver
    Og skal ha totalt 1 åpne søknadsoppgaver
    Og skal responsen fra oppgave med type 'BEH_SAK' inneholde feltet 'tildeltEnhetsnr' = '4806'
    Og skal responsen fra oppgave med type 'BEH_SAK' inneholde feltet 'aktoerId' = '2350724505015'
    Og at jeg henter endret journalpost for nøkkel 'JOARK_INNGAAENDE_JOURNALFOR'
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'gjelderAktor.ident' = '22466401394'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'tema' = 'BID'