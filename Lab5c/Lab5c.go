package main

import (
	"fmt"
	"sync"
	"time"
	"math/rand"
)

type Barrier struct {
	n uint64
	mux sync.Mutex
	currStopped uint64
    stopPoint chan int
}

func NewBarrier(n int) Barrier {
    b := Barrier{
		n: uint64(n),
		currStopped: 0,
        stopPoint: make(chan int, 1),
	}
	 
    return b
}

func (b *Barrier) Await() {
	b.mux.Lock()
	b.currStopped += 1

	if b.currStopped < b.n {
		b.mux.Unlock()
		<-b.stopPoint
		b.stopPoint<- 1
	} else {
		b.currStopped = 0

		b.stopPoint<- 1

		for len(b.stopPoint) > 1 {
		}

		<-b.stopPoint
		b.mux.Unlock()
	}
}

func sum(arr []int) float64 {
	s := 0

	for _, a := range arr {
		s = s + a
	}

	return float64(s)
}

func avg(arrays [][]int) float64 {
	s := float64(0)

	for _, array := range arrays {
		s = s + sum(array)
	}

	res := float64(s) / 3

	return res
}

func thread(arrays *[][]int, threadNum int, b *Barrier, wg *sync.WaitGroup) {
	defer wg.Done()
	for {
		s := sum((*arrays)[threadNum])

		a := avg(*arrays)

		if s == sum((*arrays)[0]) && s == sum((*arrays)[1]) && s == sum((*arrays)[2]) {
			break;
		}

		if s == a {
			b.Await();
			continue;

		} else {

			source := rand.NewSource(time.Now().UnixNano())
			r1 := rand.New(source)

			i := r1.Intn(10)

			if s - a < -0.5 {
				(*arrays)[threadNum][i] += 1
			} else if s - a > 0.5 {
				(*arrays)[threadNum][i] -= 1
			}
		}

		b.Await()
		//fmt.Printf("%d arr sum: %f\n", threadNum, s)
		//fmt.Printf("avg: %f\n", a)
	}
}

func main() {
	arrays:= [][] int {
		{93, 10, 86, 45, 56, 37, 74, 51, 58, 41},
		{88, 38, 61, 80, 56, 74, 18, 45, 86, 55},
		{54, 84, 46, 59, 91, 31, 96, 37, 81, 58},
	}

	fmt.Printf("1st array sum: %f\n", sum(arrays[0]))
	fmt.Printf("2nd array sum: %f\n", sum(arrays[1]))
	fmt.Printf("3rd array sum: %f\n", sum(arrays[2]))
	fmt.Printf("Their average: %f\n\n", avg(arrays))

	var wg sync.WaitGroup
	wg.Add(3)

	b := NewBarrier(3)

	go thread(&arrays, 0, &b, &wg)
	go thread(&arrays, 1, &b, &wg)
	go thread(&arrays, 2, &b, &wg)

	wg.Wait()

	fmt.Printf("1st array sum: %f\n", sum(arrays[0]))
	fmt.Printf("2nd array sum: %f\n", sum(arrays[1]))
	fmt.Printf("3rd array sum: %f\n", sum(arrays[2]))
	fmt.Printf("Their average: %f\n", avg(arrays))
	fmt.Printf("1st array: %v\n", arrays[0])
	fmt.Printf("2nd array: %v\n", arrays[1])
	fmt.Printf("3rd array: %v\n", arrays[2])
}