/* TimeDomainSampleFilterEngine.java created 2010-08-24
 * 
 */

package org.signalml.domain.signal;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/** TimeDomainSampleFilterEngine
 *
 * 
 * @author Piotr Szachewicz
 */
public class TimeDomainSampleFilterEngine extends SampleFilterEngine {
	
	private TimeDomainSampleFilter definition;
        //tu by trzeba zrobić, żeby nie było osobnych klas dla predefiniowanych filtrów
	
	private double[] cache = null;
	private double[] filtered = null;

        protected double aCoefficients[];
	protected double bCoefficients[];
	
	public TimeDomainSampleFilterEngine( SampleSource source, TimeDomainSampleFilter definition ) {
		super( source );
//                this.definition = new TimeDomainSampleFilter( definition );

                aCoefficients=definition.getACoefficients();
                bCoefficients=definition.getBCoefficients();
		
	}
	
	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized( this ) {
                    
			int i,j;

                        int addLeft=100;
                        if(signalOffset-addLeft<0)
                            addLeft=signalOffset;
                        int newOffset=signalOffset-addLeft;
                        int newCount=count+addLeft;


                        cache = new double[newCount];
                        filtered= new double[newCount];
                        System.out.println("FILTR: Pobrano "+count+ " /signalOffset="+signalOffset+" arrayOffset="+arrayOffset); // 900, 1800 probek
                        source.getSamples(cache, newOffset, newCount, 0);

                        for(i=0; i< newCount; i++){
                            for(j=i-bCoefficients.length+1; j<=i; j++){
                                if (j<0) j=0;
                                filtered[i]+=cache[j]*bCoefficients[i-j];
                                if (j<i)
                                    filtered[i]-=filtered[j]*aCoefficients[i-j];
                            }
                            filtered[i]/=aCoefficients[0];
                        }

                        for( i=0; i<count; i++ )
                                target[arrayOffset+i]=filtered[addLeft+i];

                        /*to get accurate results, the engine calculates the applies
                         * the filter on the whole signal.
                         
                        cache = new double[signalOffset+count];
                        filtered= new double[signalOffset+count];
                        System.out.println("FILTR: Pobrano "+count+ " /signalOffset="+signalOffset+" arrayOffset="+arrayOffset); // 900, 1800 probek
                        source.getSamples(cache, 0, signalOffset+count, 0);

                        for(i=0; i< signalOffset+count; i++){
                            for(j=i-bCoefficients.length+1; j<=i; j++){
                                if (j<0) j=0;
                                filtered[i]+=cache[j]*bCoefficients[i-j];
                                if (j<i)
                                    filtered[i]-=filtered[j]*aCoefficients[i-j];
                            }
                            filtered[i]/=aCoefficients[0];
                        }

                        for( i=0; i<count; i++ ) 
                                target[arrayOffset+i]=filtered[signalOffset+i];
                         */
						
		}
	}

}
