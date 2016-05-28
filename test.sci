// this is a definition of factorial, valid for integers
factorial := fun (n)
  if n <= 1 do
    1
  else
    n * factorial(n - 1)
  end
end;

// here's fibonacci, starting with 0, 1.
fib := fun (n)
  if n <= 0 do
    0
  elif n == 1 do
    1
  else
    fib(n-1) + fib(n-2)
  end
end;

// that fibonacci is kind of slow.  here is a faster version (not exponential time!)
fib2 := fun (n)
  a := 0;
  b := 1;
  m := 0;
  while m < n do
    c := b;
    b <- a + b;
    a <- c;
    m <- m + 1;
  end;
  a
end;

fun mat_prod(A, B)
  [[A[0,0]*B[0,0] + A[0,1]*B[1,0], A[0,0]*B[0,1] + A[0,1]*B[1,1]],
   [A[1,0]*B[0,0] + A[1,1]*B[1,0], A[1,0]*B[0,1] + A[1,1]*B[1,1]]]
end;

// and an even faster implementation (logarithmically many steps!)
fun fib3(n)
  mat := [[0, 1],
          [1, 1]];
  A := [[1, 0],
        [0, 1]];
  while n > 0 do
    if n % 2 do
      A <- mat_prod(A, mat);
    end;
    mat <- mat_prod(mat, mat);
    n <- n div 2;
  end;
  A[0,1];
end;

test_fib := fun ()
  n := 0;
  print_line("n fib fib2 fib3");
  while n < 20 do
    print_line(n, fib(n), fib2(n), fib3(n));
    n <- n + 1;
  end;
end;

test_fib();

eratosthenes := fun (n)
  arr := make_array(n, true);
  i := 2;
  while i < n do
    if arr[i] do
      print_line(i);
      j := 2*i;
      while j < n do
        arr[j] <- false;
        j <- j + i;
      end;
    end;
    i <- i + 1;
  end;
end;

print_line("primes up to 100");
eratosthenes(100);

make_matrix := fun (n, m)
  mat := [];
  i := 0;
  while i < n do
    push(mat, make_array(m, 0.0));
    i <- i + 1;
  end;
  mat
end;

print_line(make_matrix(2,3));

// test block
print_line(block ret do 22 end); // 22
print_line(block ret do ret(22); print_line(23) end); // 22 (note no 23!)
print_line(block ret do while true do ret(22) end end); // saved from an infinite loop

// quicksort
qsort := fun (arr)
  qsort_rec := fun (i, j)
    if i < j do
      pivot := arr[j];
      n := i; m := i;
      while n < j do
        if arr[n] < pivot do
          v := arr[m];
          arr[m] <- arr[n];
          arr[n] <- v;
          m <- m + 1;
        end;
        n <- n + 1;
      end;
      arr[j] <- arr[m];
      arr[m] <- pivot;
      qsort_rec(i, m-1);
      qsort_rec(m+1, j);
    end;
  end;
  qsort_rec(0, len(arr)-1);
  arr;
end;
print_line(qsort([1,2,3,4,5]));
print_line(qsort([1,3,5,2,4]));
print_line(qsort([3,5,1,2,4]));
print_line(qsort([5,4,3,2,1]));