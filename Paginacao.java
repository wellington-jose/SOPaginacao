
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;


/**
 *
 * @author wellington
 */
public class Paginacao {
    
    private static LinkedList<Integer> paginas = new LinkedList<Integer>(); //paginas requeridas pelo processo
    private static int memoria_alocada; //Valor da memoria alocada
    private static LinkedList<Integer> memoria = new LinkedList<Integer>(); //Lista com paginas na memoria

    private static void LeArquivo(String arq) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(arq);
        } catch (FileNotFoundException ex) {
            System.err.println("Arquivo nao encontrado");
            System.exit(1);
        }
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        String linha = null;
        try {
            linha = br.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (linha == null) {
            System.err.println("O arquivo esta vazio");
            System.exit(1);
        } else {
            try {
                memoria_alocada = Integer.parseInt(linha);
                if(memoria_alocada <= 0){
                    System.err.println("A quantidade de paginas nao pode ser 0 ou negativa!");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("O arquivo so pode conter numeros");
            }
        }
        try {
            linha = br.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (linha == null) {
            System.err.println("O calculo nao pode ser realizado\n eh necessario informar as paginas que serao usadas");
            System.exit(1);
        }
        while (linha != null) {
            try {
                paginas.addLast(Integer.parseInt(linha));
                if(Integer.parseInt(linha) < 0){
                    System.out.println("O valor das paginas nao podem ser negativos");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("O arquivo so pode conter numeros");
                System.exit(1);
            }
            try {
                linha = br.readLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static boolean adicionaFilaLRU(Integer pagina) {
        boolean faltou_pagina;
        if (memoria.contains(pagina)) {
            memoria.remove(pagina);
            memoria.addLast(pagina);
            faltou_pagina = false;
        } else {
            if (memoria.size() == memoria_alocada) {
                memoria.removeFirst();
            }
            memoria.add(pagina);
            faltou_pagina = true;
        }
        return faltou_pagina;
    }

    private static int algLRU() {
        int falta_paginas = 0;
        for (Integer i : paginas) {
            if (adicionaFilaLRU(i)) {
                falta_paginas++;
            }
        }
        memoria.clear();
        return falta_paginas;
    }

    private static boolean adicionaFilaFIFO(Integer pagina) {
        if (memoria.contains(pagina)) {
            return false;
        } else {
            if (memoria.size() == memoria_alocada) {
                memoria.removeFirst();
            }
            memoria.addLast(pagina);
            return true;
        }
    }

    private static int algFIFO() {
        int falta_paginas = 0;
        for (Integer i : paginas) {
            if (adicionaFilaFIFO(i)) {
                falta_paginas++;
            }
        }
        memoria.clear();
        return falta_paginas;
    }

    private static Integer verificaNaoUsoOTIMO() {
        int candidato = -1;
        for (Integer i : memoria) {
            if (!paginas.contains(i)) {
                candidato = i;
            }
        }
        return candidato;
    }

    private static Integer verificaMaisLongeOTIMO() {
        int quem_sai = -1;
        LinkedList<Integer> paginas_encontradas = new LinkedList<Integer>();
        for(Integer p: paginas){
            if(memoria.contains(p)){
                if(paginas_encontradas.isEmpty()){
                    quem_sai = p;
                    paginas_encontradas.add(p);
                }else if(!paginas_encontradas.contains(p)){
                    quem_sai = p;
                    paginas_encontradas.add(p);
                }
            }
        }
        return quem_sai;
    }
    
    private static boolean adicionaFilaOTIMO(Integer pagina){
        int quem_sai = -1;
        if(memoria.contains(pagina)){
            paginas.remove(pagina);
            return false;
        }
        if(memoria.size() == memoria_alocada){
            quem_sai = verificaNaoUsoOTIMO();
            if(quem_sai != -1){
                memoria.removeFirstOccurrence(quem_sai);
            }else{
                quem_sai = verificaMaisLongeOTIMO();
                memoria.removeFirstOccurrence(quem_sai);
            }
        }
        memoria.add(pagina);
        paginas.removeFirstOccurrence(pagina);
        return true;
    }

    private static int algOTIMO() {
        int falta_paginas = 0;
        while (!paginas.isEmpty()) {
            if (adicionaFilaOTIMO(paginas.getFirst())) {
                falta_paginas++;
            }
        }
        memoria.clear();
        return falta_paginas;
    }

    public static void main(String args[]) {
        int fifo, otimo, lru;
        Paginacao.LeArquivo("entrada.txt");
        fifo = Paginacao.algFIFO();
        lru = Paginacao.algLRU();
        otimo = Paginacao.algOTIMO();
        System.out.println("FIFO: " + String.valueOf(fifo));
        System.out.println("OTM: " + String.valueOf(otimo));
        System.out.println("LRU: " + String.valueOf(lru));
    }
    
}
