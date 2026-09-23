import type { NextRequest } from "next/server";
import { NextResponse } from "next/server";

export function proxy(request: NextRequest) {
  const token =
    request.cookies.get("BTHS_TOKEN");

  if (!token) {
    const loginUrl =
      new URL("/login", request.url);

    return NextResponse.redirect(loginUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/dashboard/:path*",
    "/hospedes/:path*",
    "/motorista/:path*"
  ],
};