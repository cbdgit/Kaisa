package com.qsc.heartratefragment;

public class Fft12 {
	
	/** 
     * һά���ٸ���Ҷ�任 
     * @param values һά���������� 
     * @return ����Ҷ�任������鼯 
     */  
	public Complex[] fft(Complex[] values) {
        int n = values.length;  
        int r = (int)(Math.log10(n)/Math.log10(2)); //���������r  
        Complex[][] temp = new Complex[r+1][n]; //������̵���ʱ����  
        Complex w = new Complex(0, 0);  //Ȩϵ��  
        temp[0] = values;  
        int x1, x2; //һ�Զ�ż�����±�ֵ  
        int p, t;   //p��ʾ��Ȩϵ��Wpn��pֵ, t������������Ӧ������ֵ  
        for(int l=1; l<=r; l++) {  
            if(l != r) {  
                for(int k=0; k<n; k++) {  
                    if(k < n/Math.pow(2, l)) {  
                        x1 = k;  
                        x2 = x1 + (int)(n/Math.pow(2, l));  
                    } else {  
                        x2 = k;  
                        x1 = x2 - (int)(n/Math.pow(2, l));  
                    }  
                    p = getWeight(k, l, r);
                    w.setRe(Math.cos(-2*Math.PI*p/n));  
                    w.setIm(Math.sin(-2*Math.PI*p/n));  
                    temp[l][k] = Complex.plus(temp[l-1][x1] , w.times(temp[l-1][x2]) );  
                      
                }  
            } else {  
                for(int k=0; k<n/2; k++) {                     
                    x1 = 2*k;  
                    x2 = 2*k+1;  
                    //System.out.println("x1:" + x1 + "  x2:" + x2);  
                    t = reverseRatio(2*k, r);  
                    p = t;  
                    w.setRe(Math.cos(-2*Math.PI*p/n));  
                    w.setIm(Math.sin(-2*Math.PI*p/n));  
                    temp[l][t] = Complex.plus(temp[l-1][x1] , w.times(temp[l-1][x2]) );  
                    t = reverseRatio(2*k+1, r);  
                    p = t;  
                    w.setRe(Math.cos(-2*Math.PI*p/n));  
                    w.setIm(Math.sin(-2*Math.PI*p/n));  
                    temp[l][t] = Complex.plus(temp[l-1][x1] , w.times(temp[l-1][x2]) );  
                }  
            }             
        }         
        return temp[r];  
    }  
	/** 
     *  ��ά���ٸ���Ҷ�任 
     * @param matrix ��ά����������      
     * @param w ͼ��Ŀ� 
     * @param h ͼ��ĸ� 
    * @return ����Ҷ�任������鼯 
     */ 
	public Complex[][] fft(Complex matrix[][], int w, int h) {  
        double r1 = Math.log10(w)/Math.log10(2.0) - (int)(Math.log10(w)/Math.log10(2.0));  
        double r2 = Math.log10(h)/Math.log10(2.0) - (int)(Math.log10(w)/Math.log10(2.0));         
        if(r1 != 0.0 || r2 != 0.0) {  
            System.err.println("����Ĳ���w��h����2��n���ݣ�");  
            return null;  
        }  
//        int r = 0;  
//        r = (int)(Math.log10(w)/Math.log10(2));  
        //�����и���Ҷ�任  
        for(int i=0; i<h; i++) {  
            matrix[i] = fft(matrix[i]);   
        }  
        //�����и���Ҷ�任  
//        int n = h;  
//        r = (int)(Math.log10(n)/Math.log10(2)); //���������r  
        Complex tempCom[] = new Complex[h];  
        for(int j=0; j<w; j++) {  
            for(int i=0; i<h; i++) {  
                tempCom[i] = matrix[i][j];  
            }  
            tempCom = fft(tempCom);   
            for(int i=0; i<h; i++) {  
                matrix[i][j] = tempCom[i];  
            }  
        }         
        return matrix;  
    }
	/** 
	 * ���Ȩϵ�� 
	 * 1.����kд��rλ�Ķ�������;2.���ö�������������r-lλ;3.��rλ�Ķ����������ص�ת;4.������ú�Ķ������������ʮ������; 
	 * @param k Ҫ��ת��ʮ������ 
	 * @param l �±�ֵ 
	 * @param r �����Ƶ�λ�� 
	 * @return ��Ȩϵ�� 
	 */  
	private int getWeight(int k, int l, int r) {  
	    int d = r-l;    //λ����  
	    k = k>>d;       
	    return reverseRatio(k, r);  
	} 
	/** 
	 * �������ж����Ƶ�ת�� ��0101��ת��1010 
	 * 1.����kд��rλ�Ķ�������;2.��rλ�Ķ����������ص�ת;3.������ú�Ķ������������ʮ������; 
	 * @param k Ҫ��ת��ʮ������ 
	 * @param r �����Ƶ�λ�� 
	 * @return ��ת���ʮ������ 
	 */  
	private int reverseRatio(int k, int r) {  
	    int n = 0;  
	    StringBuilder sb = new StringBuilder(Integer.toBinaryString(k));  
	    StringBuilder sb2 = new StringBuilder("");  
	    if(sb.length()<r) {  
	        n = r-sb.length();  
	        for(int i=0; i<n; i++) {  
	            sb.insert(0, "0");  
	        }  
	    }  
	      
	    for(int i=0; i<sb.length(); i++) {  
	        sb2.append(sb.charAt(sb.length()-i-1));  
	    }         
	    return Integer.parseInt(sb2.toString(), 2);  
	}

}
