"use client";
import TaskManager from '../components/taskmanager';
import { signIn, signOut, useSession } from "next-auth/react";


export default function Home() {

  const { data: session } = useSession();

  if (session && session.user) {
    return (
      <TaskManager />
    );
  }
}

